package controllers

import play.api._
import play.api.mvc._
import views._
import scala.util.Random
import scala.io.Source
import play.api.libs.json.Json
import java.util.Properties
import java.io.FileReader


object Application extends Controller {
  
/* add a configuration file cf.properties that should contain your fb app credentials as follows:
app_id =<your app id>
app_secret=<your app secret>
*/
  val credential = new Properties() 
  credential.load(new FileReader("cf.properties"))
  val app_id = credential.getProperty("app_id")
  val app_secret = credential.getProperty("app_secret") 
 
  def index = Action { implicit request =>
    {
   val redirect_uri = "http://localhost:9000/getToken"
   val scopes = "email,user_checkins,friends_checkins"
   val state_string = new Random().nextString(16)
   val url = "https://www.facebook.com/dialog/oauth?client_id="+ app_id +"&redirect_uri="+ redirect_uri +"&scope="+ scopes +"&state="+ state_string
   
   Redirect(url)
    }   
  }
  
  def getToken(state:String, code:String) = Action { implicit request =>
    {
   val redirect_uri = "http://localhost:9000/getToken"
   val url = "https://graph.facebook.com/oauth/access_token?client_id="+ app_id +"&redirect_uri="+ redirect_uri +"&client_secret="+app_secret+"&code="+code
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