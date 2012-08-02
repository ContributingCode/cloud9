package controllers

import play.api._
import play.api.mvc._
import views._
import scala.util.Random
import scala.io.Source
import play.api.libs.json.Json


object Application extends Controller {
  
  val APP_ID = "409606552410448"
  val app_secret = "5bec26c2aacfdf0c19ad218471b59237"
    
  def index = Action { implicit request =>
    {
   val REDIRECT_URI = "http://localhost:9000/getToken"
   val LIST_SCOPES = "email,user_checkins,friends_checkins"
   val UNIQUE_STRING = new Random().nextString(16)
   val url = "https://www.facebook.com/dialog/oauth?client_id="+APP_ID+"&redirect_uri="+REDIRECT_URI+"&scope="+LIST_SCOPES+"&state="+UNIQUE_STRING
   
   Redirect(url)
    }   
  }
  
  def getToken(state:String, code:String) = Action { implicit request =>
    {
   val REDIRECT_URI = "http://localhost:9000/getToken"
   val url = "https://graph.facebook.com/oauth/access_token?client_id="+APP_ID+"&redirect_uri="+REDIRECT_URI+"&client_secret="+app_secret+"&code="+code
   val source = Source.fromURL(url)
   val token = source.mkString
   source.close()
   val access_token=token.split("&")(0).split("=")(1)
   
   Redirect(routes.Application.parseToken(access_token)).withSession("access_token"->access_token)
     }     
 }
  
  def parseToken(access_token:String) = Action { implicit request =>
    {
   val urlToGetID = "https://graph.facebook.com/me?access_token="+access_token
   val sourceMe = Source.fromURL(urlToGetID)
   val tokenMe = sourceMe.mkString
   sourceMe.close()
   val me = Json.parse(tokenMe)
   val myname = (me\"name").as[String]
   val myid = (me\"id").as[String]
   val myemail = (me\"email").as[String]

   Ok("Welcome "+myname+" "+myid+" "+myemail).withSession(session+("me_id"->myid))
    }   
  }

  def insertLost = Action {
    Ok(html.map())
  }
  def insertFound = Action {
    Ok(html.foundmap())
  }
  def doc = Action {
    Ok(html.doc())
  }
  def wanted = Action {
    Ok(html.wanted())
  }
  def allmap(user_id:String)= Action {
    Ok(html.allmap(user_id))
  }
}