package com.gemteks.tracker.WSMS116

import com.typesafe.scalalogging.StrictLogging
import spray.json._


class Decoder extends org.loraalliance.Decoder
    with StrictLogging {

  def statusLabelled(status: Boolean): String =
    if (status) {
      "true"
    } else {
      "false"
    }

  override def execute(payload: String, format: PayloadFormat = PayloadFormat.HEX): JsObject = {
    // https://github.com/IPSO-Alliance/pub/tree/master/reg%20v1_1
    logger.debug(s"got it in the library: $payload")

    val payloadBinString = payload.foldLeft(Array[String]()){(newBytes: Array[String], char: Char) =>
      newBytes :+ Hex2Bin(char.toString.toUpperCase)
    }.mkString

    //Primary Measurements
    val lat =
      BigDecimal((if (payloadBinString.charAt(24) == '1') {
        val invertedPos = payloadBinString.substring(24,56).foldLeft(Array[String]()){(invertedPos: Array[String], b: Char) => {
          invertedPos :+ (1 ^ Bin2Dec(b.toString)).toString
        }}.mkString
        -Bin2Dec(invertedPos)
      } else {
        Bin2Dec(payloadBinString.substring(24,56))
      }) * 0.000001).setScale(6, BigDecimal.RoundingMode.HALF_UP).toDouble

    val lng =
      BigDecimal((if (payloadBinString.charAt(56) == '1') {
        val invertedPos = payloadBinString.substring(56,88).foldLeft(Array[String]()){(invertedPos: Array[String], b: Char) => {
          invertedPos :+ (1 ^ b.asDigit).toString
        }}.mkString
        -Bin2Dec(invertedPos)
      } else {
        Bin2Dec(payloadBinString.substring(56,88))
      }) * 0.000001).setScale(6, BigDecimal.RoundingMode.HALF_UP).toDouble

    //Device Operations Metadata
    val batteryInt: Int = Bin2Dec(payloadBinString.substring(16, 24))
    val battery: Float = batteryInt/100
    val batteryLabelled: String = batteryInt + "%"

    val status: Int = Bin2Dec(payloadBinString.substring(8, 11))
    val status_label: String =
      if (status == 0){
        "not fix"
      } else if (status == 1){
        "2D"
      } else if (status == 3){
        "3D"
      } else {
        "N/A"
      }
    val statusBool: Boolean = (status != 0)

    //    var reportTypeCode = bitsToInt(payloadByte.substring(11,16).split(''));
    val reportType: Int = Bin2Dec(payloadBinString.substring(11, 16))
    val reportType_label =
      if (reportType == 2){
        "Periodic mode report"
      } else if (reportType == 4){
        "Motion mode static report"
      } else if (reportType == 5){
        "Motion mode moving report"
      } else if (reportType == 6){
        "Motion mode static to motion report"
      } else if (reportType == 7){
        "Motion mode moving to static report"
      } else if (reportType == 14){
        "SOS alarm report"
      } else if (reportType == 15){
        "Low battery alarm report"
      } else if (reportType == 17){
        "Power on(temperature)"
      } else if (reportType == 19){
        "Power off(low battery)"
      } else if (reportType == 20){
        "Power off(temperature)"
      } else {
        "N/A"
      }

    logger.debug(s"folding into jsobject: $lat, $lng")

    JsObject(

      "loc_lat" -> JsNumber(lat),
      "loc_lng" -> JsNumber(lng),
      "loc_label" -> JsString(s"$lat, $lng"),

      "device" -> JsObject(
        "gps" -> JsObject(
          "state" -> JsString(status_label)
        ),
        "report" -> JsNumber(reportType),
        "report_label" -> JsString(reportType_label),
        "battery" -> JsObject(
          "power" -> JsNumber(battery),
          "power_label" -> JsString(batteryLabelled)
        )
      ),

      "ipso" -> JsObject(
        // Presence Sensor
        "3302" -> JsObject(
          "5500" -> JsBoolean(statusBool),
          "5751" -> JsString("PIR")
        ),
        // GPS Position Sensor
        "3336" -> JsObject(
          "5513" -> JsNumber(lat),
          "5514" -> JsNumber(lng),
          "5750" -> JsString(reportType_label)
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

