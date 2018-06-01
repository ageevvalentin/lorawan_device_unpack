# lorawan_device_unpack
LoRaWAN Device Unpacking Library

Open Source java-compatible library for unpacking encoded device payloads typical of LoRaWAN devices.

https://lora-alliance.org/lorawan-for-developers

Usage:

  import com.gemteks.tracker.WSMS116.Decoder

  val payload = "008464026164e7fb847923"
  val decode = Decoder
  val unpacked: JsObject = decode.execute(payload, PayloadFormat.HEX)

There is a different importable package for each device. These will be namespaced using the domains of the device vendor followed by the sensor type followed by the model number. Each package support the execute() function which will take a string of data and return a Spray Json Object.

Each Json object contains both a simplified data format and the IPSO-Alliance format https://github.com/IPSO-Alliance/pub/tree/master/reg%20v1_1.

Vendors of device and gateway may support different formats for payload input. If a Decoder class does not support the requested format, it will throw an UnsupportedFormat exception. 

Typical Payload formats:
    PayloadFormat.HEX: a hexidecimal string
    PayloadFormat.BIN: a binary compatible string
    PayloadFormat.CUSTOM: something new, something odd
    
    
