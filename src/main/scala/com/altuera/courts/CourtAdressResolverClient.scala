// © LLC "Altuera", 2019
package com.altuera.courts

import java.lang.invoke.MethodHandles

import com.altuera.courts.MagistrateProtocol._
import com.softwaremill.sttp._
import org.slf4j.LoggerFactory
import spray.json._

class CourtAdressResolverClient() {
	private val baseUri = uri"http://37.230.157.227:8088"
	private val searchUri = baseUri.path("geo/search")
	private val areaUri = baseUri.path("area/get-area")

	private val log = LoggerFactory.getLogger(MethodHandles.lookup.lookupClass)
	implicit val backend = HttpURLConnectionBackend()

	def convertAddressToCoordinates(searchString: String): Seq[CoordinatesResult] = {
		val response = sttp
			.get(searchUri.params(("format", "json"), ("q", searchString)))
			.contentType("application/json;charset=utf-8")
			.send()

		response.body match {
			case Left(obj) => {
				log.trace(obj)
				Seq.empty[CoordinatesResult]
			}
			case Right(obj) => {
				log.trace(obj)
				obj.parseJson.convertTo[Seq[CoordinatesResult]]
			}
		}
	}

	def getCourtIdByCoordinates(latitude: Double, longitude: Double): Option[Int] = {
		import spray.json._
		val response = sttp.get(areaUri.params(("lat", latitude.toString), ("lng", longitude.toString)))
			.contentType("application/json;charset=utf-8")
			.send()
		response.body match {
			case Left(obj) => {
				log.trace(obj)
				None
			}
			case Right(obj) => {
				log.trace(obj)
				val mainResultObj = obj.parseJson.convertTo[MagistrateResult]
				mainResultObj.result.magistrate_id.map(Some(_)).getOrElse(None)
			}
		}
	}
}

object CourtAdressResolverClient extends App {
	override def main(args: Array[String]): Unit = {
		val apiClient = new CourtAdressResolverClient()
		val result = apiClient.convertAddressToCoordinates("гоголя")
		if (result.size > 0) {
			val loc = result.head.loc
			val lat = loc(0)
			val lng = loc(1)
			println(s"lat = $lat")
			println(s"lng = $lng")
			println(apiClient.getCourtIdByCoordinates(lat, lng))
		}
		else {
			println("результаты не получены")
		}
	}
}
