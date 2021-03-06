package org.camunda.latera.bss.connectors.Jasper

import org.camunda.latera.bss.HttpClient.HttpProcessor
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.*

data class Export(
             val id: String,
             val status: String)

data class ExecuteReportParams(
             val reportUnitUri: String,
             val parameters: LinkedHashMap<String, Any>,
             val outputFormat: String = "pdf",
             val freshData: Boolean = false,
             val saveDataSnapshot: Boolean = false,
             val interactive: Boolean = true,
             val allowInlineScripts: Boolean = true,
             val ignorePagination: Boolean? = null,
             val pages: Int? = null,
             val async: Boolean = false,
             val transformerKey: String? = null,
             val attachmentsPrefix: String = "attachments",
             val baseUrl: String? = null)

data class ExecuteReportResponse(
             val currentPage: Int,
             val exports: Array<Export>,
             val reportURI: String,
             val requestId: String,
             val status: String)

data class Options(
             val outputFormat: String,
             val attachmentsPrefix: String,
             val baseUrl: String? = null,
             val allowInlineScripts: Boolean)

data class OutputResource(
             val contentType: String)

data class Attachment(
             val contentType: String,
             val fileName: String)

data class DetailsExport(
             val id: String,
             val options: Options,
             val status: String,
             val outputResource: OutputResource,
             val attachments: Array<Attachment>)

data class ExecutionDetailsResponse(
             val status: String,
             val totalPages: Int,
             val requestId: String,
             val reportURI: String,
             val exports: Array<DetailsExport>)

data class ErrorDescriptor(
             val message: String,
             val errorCode: String,
             val parameters: Array<String>)

data class ExecutionStatusResponse(
             val value: String,
             val errorDescriptor: ErrorDescriptor? = null)

/**
 * JasperReport class is responsible for handling reqeuests to JasperReports API.
 *
 * @property url url which will be used for sending requests to JasperReports API.
 * @property user login for JasperReports API.
 * @property password password for JasperReports API.
 * @constructor Creates JasperReport instance.
 */
class JasperReport(val url: String, val user: String, val password: String) {
  private val baseUrl: java.net.URI = java.net.URI(this.url)
  /**
   * createExecuteReportParams - method creates ExecuteReportParams object from LinkedHashMap.
   * @param LinkedHashMap<String, Any> params for request
   * @return instance of ExecuteReportParams
   */
  fun createExecuteReportParams(params: LinkedHashMap<String, Any>): ExecuteReportParams {
    return ExecuteReportParams(
             reportUnitUri = params.get("reportUnitUri") as String,
             parameters = params.get("parameters") as LinkedHashMap<String, Any>,
             outputFormat = params.get("outputFormat") as String? ?: "pdf",
             freshData = params.get("freshData") as Boolean? ?: false,
             saveDataSnapshot = params.get("saveDataSnapshot") as Boolean? ?: false,
             interactive = params.get("interactive") as Boolean? ?: true,
             allowInlineScripts = params.get("allowInlineScripts") as Boolean? ?: true,
             ignorePagination = params.get("ignorePagination") as Boolean?,
             pages = params.get("pages") as Int?,
             async = params.get("async") as Boolean? ?: false,
             transformerKey = params.get("transformerKey") as String?,
             attachmentsPrefix = params.get("attachmentsPrefix") as String? ?: "attachments",
             baseUrl = params.get("baseUrl") as String?)
  }

  /**
   * Performs 'Running a Report Asynchronously' method.
   * Runs report asynchronously.
   * @param params instance of ExecuteReportParams containing parameters for request.
   * @return instance of ExecuteReportResponse containing the response results,
   * contains the requestID needed to track the execution until completion.
   */
  fun executeReport(params: ExecuteReportParams): ExecuteReportResponse {
    val executeReportUrl: String = this.baseUrl.resolve(this.baseUrl.path + "/rest_v2/reportExecutions").toString()

    val client = HttpProcessor.getJasperClient(this.user, this.password)
    val response = runBlocking {
      client.post<ExecuteReportResponse>(executeReportUrl){
        contentType(ContentType.Application.Json)
        body = params
      }
    }

    client.close()
    return response
  }

  /**
   * Performs 'Requesting Report Output' method.
   * Downdloads report output.
   * @param requestId request id.
   * @param exportId export id.
   * @return request response.
   */
  fun getReportResult(requestId: String, exportId: String):  String {
    val reportResultUrl: String = this.baseUrl.resolve(this.baseUrl.path + "/rest_v2/reportExecutions/${requestId}/exports/${exportId}/outputResource").toString()

    val client = HttpProcessor.getJasperClient(this.user, this.password)
    val response = runBlocking {
      client.get<String>(reportResultUrl){
        contentType(ContentType.Application.Json)
      }
    }

    client.close()
    return response
  }

  /**
   * Performs 'Requesting Report Output' method.
   * Downnloads attachments.
   * @param requestId request id.
   * @param exportId export id.
   * @param fileName filename.
   * @param attachmentsPrefix attachment prefix, default value = 'attachments'.
   * @return request response.
   */
  fun getFileFromReport(requestId: String, exportId: String, fileName: String, attachmentsPrefix: String = "attachments"): String {
    val fileFromReportUrl: String = this.baseUrl.resolve(this.baseUrl.path + "/rest_v2/reportExecutions/${requestId}/exports/${exportId}/${attachmentsPrefix}/${fileName}").toString()

    val client = HttpProcessor.getJasperClient(this.user, this.password)
    val response = runBlocking {
      client.get<String>(fileFromReportUrl){
        contentType(ContentType.Application.Json)
      }
    }

    client.close()
    return response
  }

  /**
   * Performs 'Requesting Report Execution Details' method.
   * Gets report details.
   * @param requestId request id.
   * @return instance of ExecutionDetailsResponse containing the response results.
   */
  fun getReportExecutionDetails(requestId: String): ExecutionDetailsResponse {
    val executionDetailsUrl: String = this.baseUrl.resolve(this.baseUrl.path + "/rest_v2/reportExecutions/${requestId}").toString()

    val client = HttpProcessor.getJasperClient(this.user, this.password)
    val response = runBlocking {
      client.get<ExecutionDetailsResponse>(executionDetailsUrl){
        contentType(ContentType.Application.Json)
      }
    }

    client.close()
    return response
  }

  /**
   * Performs 'Polling Report Execution' method.
   * Gets status of the report execution.
   * @param requestId request id.
   * @return instance of ExecutionStatusResponse containing the response results.
   */
  fun getReportExecutionStatus(requestId: String): ExecutionStatusResponse {
    val executionStatusUrl: String = this.baseUrl.resolve(this.baseUrl.path + "/rest_v2/reportExecutions/${requestId}/status").toString()

    val client = HttpProcessor.getJasperClient(this.user, this.password)
    val response = runBlocking {
      client.get<ExecutionStatusResponse>(executionStatusUrl){
        contentType(ContentType.Application.Json)
      }
    }

    client.close()
    return response
  }
}
