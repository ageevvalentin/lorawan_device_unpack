package io.tracknet

import spray.json.{JsNumber, JsObject, JsString}

trait Common extends org.loraalliance.Helpers {

  // Decode standard Tabs sensor battery statistics and return in both regularized and IPSO Json formats
  def batteryDecode(tabsPayload: String): (JsObject, JsObject) = {
    //Device Operations Metadata
    val battery: Double = (Hex2Dec(tabsPayload.substring(2, 3)) / 15.0)
    val batteryInt: Int = Math.round(100 * (Hex2Dec(tabsPayload.substring(2, 3)) / 15.0)).toInt
    val batteryVoltage: Double = (25 + Hex2Dec(tabsPayload.substring(3, 4)).toDouble) / 10
    val batteryLabelled: String = battery + "%"
    val batteryVoltageLabelled: String = batteryVoltage + "V"
    (
      // Normalized
      JsObject(
        "battery" -> JsObject(
          "power" -> JsNumber(batteryInt),
          "power_label" -> JsString(batteryLabelled),
          "voltage" -> JsNumber(batteryVoltage),
          "voltage_label" -> JsString(batteryVoltageLabelled)
          )
      ),
      // IPSO
      JsObject(
        "ipso" -> JsObject(
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
    )
  }
}
