package io.tracknet.healthy.TBHV100

import com.typesafe.scalalogging.StrictLogging
import spray.json._

class Decoder extends org.loraalliance.Decoder with StrictLogging {

  def statusLabelled(status: Boolean): String =
    if (status) {
      "true"
    } else {
      "false"
    }

  override def execute(tabsPayload: String, format: PayloadFormat = PayloadFormat.HEX): JsObject = {
    // https://github.com/IPSO-Alliance/pub/tree/master/reg%20v1_1

    val payload = if (format == PayloadFormat.BASE64) {
      atoh(tabsPayload)
    } else {
      tabsPayload
    }

    logger.debug(payload)

    /* Decoding below assumes that we have a hexstring in the payload */

    //Primary Measurements
    val status: Int = Hex2Dec(payload.substring(0, 2))
    val statusBool: Boolean = (status == 0)
    logger.debug(s"Status: $status")

    // Convert Fahrenheit to Celsius
    val temp: Int = Hex2Dec(payload.substring(4, 6)) - 32
    val tempLabelled: String = temp + " °C"
    logger.debug(s"Temp: $temp")

    val rh: Int = Hex2Dec(payload.substring(6,8))
    val rhLabelled: String = rh + "%"

    val co2: Int = Hex2Dec(payload.substring(10,12) + payload.substring(8,10))
    val co2Labelled: String = co2 + "ppm"

    val voc: Int = Hex2Dec(payload.substring(14,16) + payload.substring(12,14))
    val vocLabelled: String = voc + "ppb"

    //Device Operations Metadata
    val battery: Double = (Hex2Dec(payload.substring(2, 3)) / 15.0)
    val batteryInt: Int = Math.round(100 * (Hex2Dec(payload.substring(2, 3)) / 15.0)).toInt
    val batteryVoltage: Double = (25 + Hex2Dec(payload.substring(3, 4)).toDouble) / 10
    val batteryLabelled: String = battery + "%"
    val batteryVoltageLabelled: String = batteryVoltage + "V"

    /* Adding decoded information into the standard JSON structure */

    JsObject(
      "status" -> JsNumber(status),
      "status_label" -> JsString(statusLabelled(statusBool)),

      "temp" -> JsNumber(temp),
      "temp_label" -> JsString(tempLabelled),
      "temp_unit" -> JsString("°C"),

      "humidity" -> JsNumber(rh),
      "humidity_label" -> JsString(rhLabelled),
      "humidity_unit" -> JsString("%"),

      "co2" -> JsNumber(co2),
      "co2_label" -> JsString(co2Labelled),
      "co2_unit" -> JsString("ppm"),

      "voc" -> JsNumber(voc),
      "voc_label" -> JsString(vocLabelled),
      "voc_unit" -> JsString("ppb"),

      "device" -> JsObject(
        "battery" -> JsObject(
          "power" -> JsNumber(batteryInt),
          "power_label" -> JsString(batteryLabelled),
          "voltage" -> JsNumber(batteryVoltage),
          "voltage_label" -> JsString(batteryVoltageLabelled)
        )
      ),

      "ipso" -> JsObject(
        // Presence Sensor
        "3302" -> JsObject(
          "5500" -> JsBoolean(statusBool),
          "5751" -> JsString("PIR")
        ),
        // Temperature Sensor
        "3303" -> JsObject(
          "5700" -> JsNumber(temp),
          "5701" -> JsString("Cel")
        ),
        // Humidity Sensor
        "3304" -> JsObject(
          "5700" -> JsNumber(rh),
          "5701" -> JsString("%")
        ),
        // Concentration: CO2 Sensor
        "3325-1" -> JsObject(
          "5700" -> JsNumber(co2),
          "5701" -> JsString("ppm"),
          "5750" -> JsString("concentration of CO2 in ppm")
        ),
        // Concentration: VoC Sensor
        "3325-2" -> JsObject(
          "5700" -> JsNumber(co2),
          "5701" -> JsString("ppb"),
          "5750" -> JsString("concentration of VOCs in ppb")
        ),
        // Voltage Sensor
        "3316" -> JsObject(
          "5700" -> JsNumber(batteryVoltage),
          "5701" -> JsString("V"),
          "5750" -> JsString("device battery state: voltage")
        ),
        // Percentage Charged Sensor
        "3320" -> JsObject(
          "5700" -> JsNumber(battery),
          "5701" -> JsString("%"),
          "5750" -> JsString("device battery state: percentage charged")
        )
      )
    )
  }

}

