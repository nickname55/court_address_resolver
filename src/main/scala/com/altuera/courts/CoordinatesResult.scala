// © LLC "Altuera", 2019
package com.altuera.courts

import spray.json.DefaultJsonProtocol._
import spray.json._

final case class CoordinatesResult(loc: Vector[Double], title: String)

object CoordinatesResult {

	implicit object CustomNoticeResponse extends RootJsonFormat[CoordinatesResult] {
		def write(c: CoordinatesResult): JsValue = JsObject(
			"title" -> JsString(c.title),
			"loc" -> JsArray(c.loc.map(_.toJson)) // log(0) is latitude value, loc(1) is longitude value
		)

		def read(value: JsValue): CoordinatesResult = {
			value.asJsObject.getFields("loc", "title") match {
				case Seq(JsArray(loc), JsString(title)) =>
					new CoordinatesResult(loc.map(_.convertTo[Double]), title)
				case _ => throw new DeserializationException("Coordinates result object expected")
			}
		}
	}

}
