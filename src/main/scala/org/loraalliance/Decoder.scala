package org.loraalliance

import spray.json.JsObject

trait Decoder extends Helpers {

  type PayloadFormat = Int
  object PayloadFormat {
    val HEX: PayloadFormat = 1
    val BIN: PayloadFormat = 2
    val CUSTOM: PayloadFormat = 3
  }

  def execute(tabsPayload: String, format: PayloadFormat = PayloadFormat.HEX): JsObject = ???

}
