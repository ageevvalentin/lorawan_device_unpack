package org.loraalliance

import org.apache.commons.codec.binary.Base64
import spray.json._
import DefaultJsonProtocol._

class Helpers {

  // Decoding a base64 encoded String
  def atob(base64String: String): String = {
    new String(Base64.decodeBase64(base64String))
  }

  //Encoding a String to base64
  def btoa(string: String): String = {
    Base64.encodeBase64String(string.getBytes("utf-8"))
  }

  def Hex2Bin(hexadecimalString: String): String = {
    val binLength: Int = hexadecimalString.length * 4
    val binString: String = Integer.parseInt(hexadecimalString, 16).toBinaryString
    prependUpString(binString, binLength, '0')
  }

  def Hex2Dec(hexadecimalString: String): Int = {
    Integer.parseInt(hexadecimalString, 16)
  }

  // Signed decimal from the 2's complement
  def Hex2SignedDec(hexadecimalString: String): Int = {
    Bin2SignedDec(Hex2Bin(hexadecimalString))
  }

  def Bin2Hex(binaryString: String): String = {
    val hexString: String = Integer.parseInt(binaryString, 2).toHexString.toUpperCase
    if (hexString.length % 2 != 0)
      prependUpString(hexString, hexString.length + 1, '0')
    else
      hexString
  }

  def Bin2Dec(binaryString: String): Int = {
    Integer.parseInt(binaryString, 2)
  }

  // Signed decimal from the 2's complement
  def Bin2SignedDec(binaryString: String): Int = {
    if (binaryString.charAt(0) == '1')
    // Correct 2's complement decoding
      - 1 - Bin2Dec(
        binaryString.substring(1).foldLeft(""){
          (accumulator, bit) => accumulator + Math.abs(1-Integer.parseInt(bit.toString, 10))
        }
      )
    else
      Integer.parseInt(binaryString, 2)
  }

  // TODO should it support non Int ?
  def Dec2Hex(decimalNumber: Int): String = {
    val hexString: String = decimalNumber.toHexString.toUpperCase
    if (hexString.length % 2 != 0)
      prependUpString(hexString, hexString.length + 1, '0')
    else
      hexString
  }

  // Suppose it's bytes (so result is multiple of 8 length)
  def Dec2Bin(decimalNumber: Int): String = {
    val binaryString: String = decimalNumber.toBinaryString
    if (binaryString.length % 8 != 0)
      prependUpString(binaryString, binaryString.length + (8 - binaryString.length % 8), '0')
    else
      binaryString
  }

  def Dec2HexTwoCompl(decimalNumber: Int): String = {
    if (decimalNumber<0){
      Bin2Hex(Dec2BinTwoCompl(decimalNumber))
    } else {
      Dec2Hex(decimalNumber)
    }
  }

  def Dec2BinTwoCompl(decimalNumber: Int): String = {
    if (decimalNumber<0){
      Dec2Bin(Math.abs(decimalNumber) - 1).foldLeft(""){
        (accumulator, bit) => accumulator + Math.abs(1-Integer.parseInt(bit.toString, 10))
      }
    } else {
      Dec2Bin(decimalNumber)
    }
  }

  // Private Helpers
  private def prependUpString(originalSring: String, finalLength: Int, paddingCharacter: Char): String ={
    originalSring.reverse.padTo(finalLength, paddingCharacter).reverse.mkString
  }

  private def buildComplexLppMeasureString(sensorFields: List[(JsValue, Double)], lppSize: Int): String = {
    sensorFields.foldLeft("") {
      (sensorMeasuresEncoded, nextField) =>
        sensorMeasuresEncoded + encodedMeasure(nextField._1.convertTo[Double], nextField._2, lppSize*2/3)
    }
  }

  private def encodedMeasure(measure: Double, resolution: Double, size: Int): String ={
    prependUpString(
      Dec2HexTwoCompl(Math.round(measure / resolution).toInt),
      size,
      '0'
    )
  }
}

