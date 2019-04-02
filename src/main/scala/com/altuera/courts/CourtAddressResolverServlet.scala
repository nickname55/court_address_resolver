// © LLC "Altuera", 2019
package com.altuera.courts

import javax.servlet.ServletConfig
import javax.servlet.annotation.WebServlet
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import org.slf4j.LoggerFactory
import spray.json.{JsObject, _}

@WebServlet(name = "CourtAddressResolverServlet", urlPatterns = {
	Array("/court-address-resolver/*")
}, loadOnStartup = 1)
class CourtAddressResolverServlet extends HttpServlet {

	private val log = LoggerFactory.getLogger(this.getClass)
	val apiClient = new CourtAdressResolverClient()

	override def init(config: ServletConfig) = {

	}

	override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {

		resp.setContentType("application/json")
		resp.setCharacterEncoding("UTF-8")

		resp.setStatus(HttpServletResponse.SC_OK)
		log.trace("send data to client")

		val result = try {
			val searchString = req.getParameter("search")
			if (searchString != null && searchString.length > 0) {
				val result: Seq[CoordinatesResult] = apiClient.convertAddressToCoordinates(searchString)
				if (result.size > 0) {
					log.trace("результаты получены, формируем ответ")
					convertResultToResponse(result)
				}
				else {
					log.trace("result not found")
					makeResponseJsObject("ok", "not found")
				}
			}
			else {
				log.trace("не заполнен параметр search")
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST)
				makeResponseJsObject("error", "parameter [search] is empty or null")
			}
		}
		catch {
			case t: Throwable => {
				log.error("возникла ошибка", t)
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
				makeResponseJsObject("error", t.getLocalizedMessage)
			}
		}
		resp.getOutputStream.print(result.toString())
	}

	private def convertResultToResponse(result: Seq[CoordinatesResult]): JsObject = {
		val loc = result.head.loc
		val latitude = loc(0)
		val longitude = loc(1)
		log.trace(s"latitude = $latitude")
		log.trace(s"longitude = $longitude")
		val courtId: Option[Int] = apiClient.getCourtIdByCoordinates(latitude, longitude)
		log.trace(s"courtId=$courtId")
		val resultString = "77MS".concat("%04d".format(courtId.getOrElse(0)).toString)
		log.trace(s"resultString=$resultString")
		makeResponseJsObject("ok", data = resultString)
	}

	def makeResponseJsObject(result: String = "", message: String = "", data: String = ""): JsObject = {
		JsObject("result" -> JsString(result), "message" -> JsString(message), "data" -> JsString(data))
	}

	override def destroy() = {
	}
}
