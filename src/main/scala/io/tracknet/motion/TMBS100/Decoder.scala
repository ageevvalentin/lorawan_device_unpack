package io.tracknet.motion.TMBS100

import io.tracknet.Common
import spray.json._

class Decoder extends org.loraalliance.Decoder with Common {

  def statusLabelled(status: Boolean): String =
    if (status) {
      "free"
    } else {
      "occupied"
    }

  override def execute(tabsPayload: String, format: PayloadFormat = PayloadFormat.HEX): JsObject = {
    // https://github.com/IPSO-Alliance/pub/tree/master/reg%20v1_1

    //Primary Measurements
    val status: Int = Hex2Dec(tabsPayload.substring(0, 2))
    val statusBool: Boolean = (status == 0)

    val temp: Int = Hex2Dec(tabsPayload.substring(4, 6)) - 32
    val tempLabelled: String = temp + "°C"

    //Device Operations Metadata
//    val batteryObjTup = batteryDecode(tabsPayload)
    val battery: Double = (Hex2Dec(tabsPayload.substring(2, 3)) / 15.0)
    val batteryInt: Int = Math.round(100 * (Hex2Dec(tabsPayload.substring(2, 3)) / 15.0)).toInt
    val batteryVoltage: Double = (25 + Hex2Dec(tabsPayload.substring(3, 4)).toDouble) / 10
    val batteryLabelled: String = batteryInt + "%"
    val batteryVoltageLabelled: String = batteryVoltage + "V"

    val time: Int = Hex2Dec(tabsPayload.substring(8, 10) + tabsPayload.substring(6, 8))
    val timeLabelled: String = time + " minutes"

    val count: Int = Hex2Dec(tabsPayload.substring(14, 16) + tabsPayload.substring(12, 14) + tabsPayload.substring(10, 12))

    JsObject(
      "status" -> JsNumber(status),
      "status_label" -> JsString(statusLabelled(statusBool)),
      "temp" -> JsNumber(temp),
      "temp_label" -> JsString(tempLabelled),
      "temp_unit" -> JsString("°C"),
      "time" -> JsNumber(time),
      "time_unit" -> JsString("minutes"),
      "time_label" -> JsString(timeLabelled),
      "device" -> JsObject(
        "battery" -> JsObject(
          "power" -> JsNumber(battery),
          "power_label" -> JsString(batteryLabelled),
          "voltage" -> JsNumber(batteryVoltage),
          "voltage_label" -> JsString(batteryVoltageLabelled)
        ),
        "count" -> JsNumber(count)
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
        ),
        // Time Sensor
        "3333" -> JsObject(
          "5707" -> JsNumber(time * 60),
          "5750" -> JsString("seconds since last state change")
        ),
        // Count - Generic Sensor
        "3000" -> JsObject(
          "5534" -> JsNumber(count),
          "5750" -> JsString("count of reports since power-on")
        )
      )
    )
  }

}

