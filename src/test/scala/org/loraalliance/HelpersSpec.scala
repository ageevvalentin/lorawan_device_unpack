package org.loraalliance
// ~testOnly org.loraalliance.HelpersSpec

import com.typesafe.scalalogging.StrictLogging
import org.apache.commons.codec.binary.Base64
import org.scalatest.{Matchers, WordSpecLike}

class HelpersSpec
  extends Helpers
    with WordSpecLike
    with Matchers
    with StrictLogging
{

  val payload_hex = "MDAyODNiMGE4NWY3N2IzNzAwMDAwMDAwMDAwMGZmZmY="  // reference base64
  val payload_unpacked = "00283b0a85f77b37000000000000ffff"         // expected hex

  val payload_hex_femto = "nhxICQcQABNRUSYAAA53"  // reference base64
  val payload_unpacked_femto = "9E1C48090710001351512600000E77".toLowerCase         // expected hex

  "Using the Helper Functions" should {

    "Encode a a string to base64" in {
      payload_hex should be (btoa(payload_unpacked))
    }

    "Decode a base64 encoded string " in {
      payload_unpacked should be (atob(payload_hex))
    }

    "Decode a funky base64 encoded string " in {
      payload_unpacked_femto should be (atoh(payload_hex_femto))
    }

    "Decode a hex encoded string to binary string" in {
      logger.debug(s"Hex2Bin: ${Hex2Bin(payload_unpacked.substring(2, 4))}")
      Hex2Bin(payload_unpacked.substring(2, 4)) should be ("00101000")
    }

    "Decode a hex encoded string to decimal number" in {
      logger.debug(s"Hex2Dec: ${Hex2Dec(payload_unpacked.substring(2, 4))}")
      Hex2Dec(payload_unpacked.substring(2, 4)) should be (40)
    }
  }

}
