package ch.parametric.counter.PCR1LR

import com.typesafe.scalalogging.StrictLogging
import spray.json._

class Decoder extends org.loraalliance.Decoder with StrictLogging {
  override def execute(payload: String, format: PayloadFormat = PayloadFormat.HEX): JsObject = {
    logger.debug(s"Payload: $payload")

    if (format != PayloadFormat.HEX) {
      throw new IllegalStateException(s"Unsupported format $format")
    }

    val value = Hex2Dec(payload.substring(2, 6))

    val result = JsObject(
      "count" -> JsNumber(value),
      "payload" -> JsString(payload),
      "device" -> JsObject(),

      "ipso" -> JsObject(
        // Counter Sensor
        "3300" -> JsObject(
          "5534" -> JsNumber(value),
          "5750" -> JsString("count of persons since last transmit")
        )
      )
    )

    logger.debug(s"Result: $result")

    result
  }
}
