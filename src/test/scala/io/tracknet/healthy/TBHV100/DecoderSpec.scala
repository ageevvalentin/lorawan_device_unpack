package io.tracknet.healthy.TBHV100

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{Matchers, WordSpecLike}
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.io.Source

// ~testOnly io.tracknet.healthy.TBHV100.DecoderSpec

class DecoderSpec
  extends WordSpecLike
    with Matchers
    with StrictLogging
    {

      //TODO: expand testing data using a file of payload and expected values
      //      val testPayloads: JsArray = Source.fromFile(s"src/test/resources/files/WSMS116_testdata.json").mkString.parseJson.convertTo[JsArray]
      val payloads = Vector("0025313B48021C00")
      //echo -n "Q0FDQTA=" | base64 -D | hexdump -C
      val payload_hex = "APs8IP////8="
      val testPayloads: JsArray = JsArray(payloads.map{payload: String => JsObject("payload" -> JsString(payload))})
      val testHexPayloads: JsArray = JsArray(JsObject("payload" -> JsString(payload_hex)))

      "Using the Tracknet HealthHome Model TBHV100" should {

        "Decode a payload with motion" in {
          logger.debug(s"${this.getClass.getCanonicalName}")
          val decode = new Decoder
          // Loop through the captured comparison payloads
          testPayloads.elements.map(test => {
            val payload = test.asJsObject.fields.get("payload").get.convertTo[String]
            val result: JsObject = decode.execute(payload)
            logger.debug(s"${result.prettyPrint}")
            //            val resultValues = result.getFields("loc_lat", "loc_lng", "device") match {
            //              case Seq(lat: JsNumber, lng: JsNumber, device_meta: JsObject) => {
            //                logger.debug(s"decoder: $lat, $lng")
            //                (lat.convertTo[Double], lng.convertTo[Double],
            //                  device_meta.fields.get("report_label").get,
            //                  device_meta.fields.get("battery").get.asJsObject.fields.get("power_label").get)
            //              }
            //            }
            //            val testValues = test.asJsObject.getFields("lat", "lng", "reportType", "battery") match {
            //              case Seq(lat: JsNumber, lng: JsNumber, rpt: JsString, pwr: JsString) => {
            //                logger.debug(s"test: $lat, $lng")
            //                (lat.convertTo[Double], lng.convertTo[Double], rpt, pwr)
            //              }
            //            }
            //            resultValues._1 should be(testValues._1 +- 0.000001) // lat
            //            resultValues._2 should be(testValues._2 +- 0.000001) // long
            //            resultValues._3 should be(testValues._3) // report type
            //            resultValues._4 should be(testValues._4) // battery percentage
            result.fields.keySet should contain("ipso")
          })
        }

        // This test accommodates network servers like KERLINK SPN which may base64 encode the device payload
        "Decode a hex payload with motion" in {
          logger.debug(s"${this.getClass.getCanonicalName}")
          val decode = new Decoder
          // Loop through the captured comparison payloads
          testHexPayloads.elements.map(test => {
            val payload = test.asJsObject.fields.get("payload").get.convertTo[String]
            logger.debug(s"input payload: ${payload}")
            val result: JsObject = decode.execute(payload, decode.PayloadFormat.BASE64)
            logger.debug(s"output payload: ${result.prettyPrint}")
            //            val resultValues = result.getFields("loc_lat", "loc_lng", "device") match {
            //              case Seq(lat: JsNumber, lng: JsNumber, device_meta: JsObject) => {
            //                logger.debug(s"decoder: $lat, $lng")
            //                (lat.convertTo[Double], lng.convertTo[Double],
            //                  device_meta.fields.get("report_label").get,
            //                  device_meta.fields.get("battery").get.asJsObject.fields.get("power_label").get)
            //              }
            //            }
            //            val testValues = test.asJsObject.getFields("lat", "lng", "reportType", "battery") match {
            //              case Seq(lat: JsNumber, lng: JsNumber, rpt: JsString, pwr: JsString) => {
            //                logger.debug(s"test: $lat, $lng")
            //                (lat.convertTo[Double], lng.convertTo[Double], rpt, pwr)
            //              }
            //            }
            //            resultValues._1 should be(testValues._1 +- 0.000001) // lat
            //            resultValues._2 should be(testValues._2 +- 0.000001) // long
            //            resultValues._3 should be(testValues._3) // report type
            //            resultValues._4 should be(testValues._4) // battery percentage
            result.fields.keySet should contain("ipso")
          })
        }
      }
}
