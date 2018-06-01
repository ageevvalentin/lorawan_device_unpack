package com.gemteks.tracker.WSMS116

import com.typesafe.scalalogging.StrictLogging
import org.scalatest.{Matchers, WordSpecLike}
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.io.Source

// ~testOnly com.gemteks.tracker.WSMS116.DecoderSpec

class DecoderSpec
  extends WordSpecLike
    with Matchers
    with StrictLogging
    {

      val testPayloads: JsArray = Source.fromFile(s"src/test/resources/files/WSMS116_testdata.json").mkString.parseJson.convertTo[JsArray]


      val payload = "008464026164e7fb847923"

      "Using the Gemteks Tracker Model WSMS116" should {

        "Decode a payload with GPS" in {
          logger.debug(s"${this.getClass.getCanonicalName}")
          val decode = new Decoder
          // Loop through the captured comparison payloads
          testPayloads.elements.map(test => {
            val payload = test.asJsObject.fields.get("payload").get.convertTo[String]
            val result: JsObject = decode.execute(payload)
            //        logger.debug(s"${result.prettyPrint}")
            val resultValues = result.getFields("loc_lat", "loc_lng", "device") match {
              case Seq(lat: JsNumber, lng: JsNumber, device_meta: JsObject) => {
                logger.debug(s"decoder: $lat, $lng")
                (lat.convertTo[Double], lng.convertTo[Double],
                  device_meta.fields.get("report_label").get,
                  device_meta.fields.get("battery").get.asJsObject.fields.get("power_label").get)
              }
            }
            val testValues = test.asJsObject.getFields("lat", "lng", "reportType", "battery") match {
              case Seq(lat: JsNumber, lng: JsNumber, rpt: JsString, pwr: JsString) => {
                logger.debug(s"test: $lat, $lng")
                (lat.convertTo[Double], lng.convertTo[Double], rpt, pwr)
              }
            }
            resultValues._1 should be(testValues._1 +- 0.000001) // lat
            resultValues._2 should be(testValues._2 +- 0.000001) // long
            resultValues._3 should be(testValues._3) // report type
            resultValues._4 should be(testValues._4) // battery percentage
            result.fields.keySet should contain("ipso")
          })
        }
      }
}
