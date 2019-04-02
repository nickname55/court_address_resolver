// © LLC "Altuera", 2019
package com.altuera.courts

import spray.json.DefaultJsonProtocol

final case class MagistrateResult(result: Magistrate)

final case class Magistrate(magistrate_id: Option[Int], description: Option[String])

object MagistrateProtocol extends DefaultJsonProtocol {
	implicit val magistrateFormat = jsonFormat2(Magistrate)
	implicit val magistrateResultFormat = jsonFormat1(MagistrateResult)
}

