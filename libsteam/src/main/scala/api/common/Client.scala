package api.common.client 

import scala.collection.immutable.ListMap

import java.net.{URISyntaxException, URI}
import java.io.{IOException, BufferedReader, InputStreamReader}

import org.slf4j.LoggerFactory
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.{ClientProtocolException, HttpResponseException}
import org.apache.http.impl.client.{HttpClientBuilder, CloseableHttpClient}

import com.google.gson.stream.{JsonReader, JsonToken}
import com.google.gson.{JsonSyntaxException, TypeAdapter, 
                        GsonBuilder, JsonParseException}

import api.common.data._

//
// Provides a method to instantiate 
// an HTTP client as provided by the Apache library
//
trait RequiresHttpClient {
  protected def getClient: CloseableHttpClient = {
    val client = HttpClientBuilder.create
    val params = 
      RequestConfig
        .custom
        .setConnectTimeout(10000)
        .setConnectionRequestTimeout(10000)
        .setSocketTimeout(10000).build
    client.setDefaultRequestConfig(params).build
  }
}

//
// A generic client that can interact with 
// Steam's API
//
trait SteamApiClient[A] 
{ 
  self: RequiresHttpClient =>

  protected val resultKey: String
  
  protected val logger    = LoggerFactory.getLogger("api.common")
  protected def base      = (new URIBuilder).setScheme("http").setHost("api.steampowered.com")

  protected def buildUrlPath(endpoint: String): URIBuilder

  //
  // (Internal) Internal way of accessing the Steam Web API.
  // Specify a type adapter (see GSON, Dota2ApiJson.scala) to convert the JSON data.
  //
  protected def getResource[T <: SteamApiDataObject](
    endpoint: String, 
    params: ListMap[String, String], 
    adapter: TypeAdapter[T])
  : Either[T, Exception] = {
    val client  = this.getClient

    try {
      val url = this.getUrl(endpoint, params) match {
        case Left(url) => url
        case Right(ex) => return Right(ex)
      }

      logger.debug(s"GET: ${url}")

      val req     = new HttpGet(url)
      val res     = client.execute(req)

      if (res.getStatusLine.getStatusCode != 200) 
        return Right(
          new HttpResponseException(res.getStatusLine.getStatusCode, 
            res.getStatusLine.getReasonPhrase))

      val reader = new JsonReader(new InputStreamReader(res.getEntity.getContent))

      this.handleJson[T](reader, adapter)
    } catch {
      case ex: IOException => Right(ex)
      case ex: ClientProtocolException => Right(ex)
      case ex: JsonSyntaxException => Right(ex)
    } finally {
      client.close
    }
  }

  //
  // (Internal) Handles the outer most JSON object, and calls the adapter on 
  // the key `result`.
  //
  private def handleJson[T <: SteamApiDataObject](
    reader: JsonReader, 
    adapter: TypeAdapter[T])
  : Either[T, Exception] = {
    val gson = (new GsonBuilder)
      .registerTypeAdapter(classOf[SteamApiData], adapter) 
      .create
    if (reader.peek.equals(JsonToken.BEGIN_OBJECT)) {
      reader.beginObject
      while (!reader.peek.equals(JsonToken.END_OBJECT)) {
        if (reader.peek.equals(JsonToken.NAME)) {
          if (reader.nextName.equals(this.resultKey)) {
            return Left(gson.fromJson(reader, classOf[SteamApiData])) 
          } else { reader.skipValue }
        } else { reader.skipValue }
      }
      reader.endObject
    } 
    Right(new JsonParseException("expected the start of an object"))
  }

  //
  // (Internal) Builds the URL from an endpoint and Map of parameters
  //
  private def getUrl(
    endpoint: String, 
    params: ListMap[String, String])
  : Either[URI, Exception] = {
    try {
      val url = params.foldLeft(
        this.buildUrlPath(endpoint))(
        (acc, pair) => acc.setParameter(pair._1, pair._2))
      Left(url.build)
    } catch {
      case ex: URISyntaxException => Right(ex)
    }
  }
}
