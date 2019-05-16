package org.camunda.latera.bss.connectors

import org.camunda.latera.bss.http.HTTPRestProcessor
import org.camunda.latera.bss.logging.SimpleLogger
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.latera.bss.utils.Order
import org.camunda.latera.bss.utils.JSON
import org.camunda.latera.bss.utils.Base64Converter

class HOMS {
  String url
  String user
  private String password
  HTTPRestProcessor http
  DelegateExecution execution
  String homsOrderCode
  String homsOrderId
  SimpleLogger logger

  HOMS(DelegateExecution execution) {
    this.execution  = execution
    this.logger     = new SimpleLogger(this.execution)
    def ENV         = System.getenv()

    this.url        = ENV['HOMS_URL']      ?: execution.getVariable("homsUrl")
    this.user       = ENV['HOMS_USER']     ?: execution.getVariable("homsUser")
    this.password   = ENV['HOMS_PASSWORD'] ?: execution.getVariable("homsPassword")
    def supress     = execution.getVariable('homsOrderSupress') ?: false

    this.http       = new HTTPRestProcessor(
      baseUrl   : this.url,
      user      : this.user,
      password  : this.password,
      supressRequestBodyLog:  supress,
      supressResponseBodyLog: supress,
      execution : this.execution
    )
    this.homsOrderCode = execution.getVariable('homsOrderCode')
    this.homsOrderId   = execution.getVariable('homsOrderId')
  }

  def createOrder(String type, LinkedHashMap data) {
    LinkedHashMap body = [
      order: [
        order_type_code: type,
        data: data
      ]
    ]
    logger.info("/ Creating new order ...")
    def result = http.sendRequest(
      'post',
      path: '/api/orders',
      supressRequestBodyLog:  false,
      supressResponseBodyLog: false,
      body: body
    )
    LinkedHashMap order = result.order
    homsOrderCode = order.code
    homsOrderId   = order.id
    execution.setVariable("homsOrderCode", homsOrderCode)
    execution.setVariable("homsOrderId",   homsOrderId)
    logger.info("\\ Order created")
    return order
  }

  void startOrder() {
    def initiatorEmail = execution.getVariable("initiatorEmail")
    LinkedHashMap body = [
      order: [
        state      : "in_progress",
        done_at    : null,
        bp_id      : execution.getProcessInstanceId(),
        bp_state   : "in_progress",
        user_email : initiatorEmail,
      ]
    ]
    logger.info("/ Starting order ...")
    def result = this.http.sendRequest(
      'put',
      path: "/api/orders/${homsOrderCode}",
      body: body
    )
    logger.info('\\ Order started')
  }

  void saveOrderData() {
    def orderData = Order.getData(execution)
    LinkedHashMap body = [
      order: [
        data: orderData
      ]
    ]
    logger.info('/ Saving order data...')
    def result = this.http.sendRequest(
      'put',
      path: "/api/orders/${homsOrderCode}",
      body: body
    )
    logger.info('\\ Order data saved')
  }

  void getOrderData() {
    logger.info('/ Receiving order data...')
    def result = this.http.sendRequest(
      'get',
      path: "/api/orders/${homsOrderCode}"
    )
    homsOrderId = result.order.id
    execution.setVariable('homsOrderId', homsOrderId)

    LinkedHashMap orderData = [:]
    result.order.data.each { k,v -> //Magic, do not touch
      orderData[k] = v
    }
    Order.saveData(orderData, execution)
    execution.setVariable('homsOrdDataBuffer', orderData)
    logger.info('\\ Order data received')
  }

  void finishOrder() {
    LinkedHashMap body = [
      order: [
        state:    "done",
        bp_state: "done",
        done_at:  String.format("%tFT%<tRZ", Calendar.getInstance(TimeZone.getTimeZone("Z")))
      ]
    ]
    logger.info("/ Finishing order ...")
    def result = this.http.sendRequest(
      'put',
      path: "/api/orders/${homsOrderCode}",
      body: body
    )
    logger.info('\\ Order finished')
  }

  def attachFiles(List files, Boolean save = true) {
    files.eachWithIndex { item, i ->
      def file = [name: item.name]
      file.content = Base64Converter.to(item.content)
      files[i] = file
    }
    LinkedHashMap body = [
      files: JSON.to(files)
    ]
    logger.info("Attaching files to order ${homsOrderId}")
    def newFiles =  this.http.sendRequest(
      'post',
      path: '/widget/file_upload',
      body: body
    )
    if (save) {
      def existingFiles = JSON.from(execution.getVariable('homsOrderDataFileList'))
      def newList = existingFiles + newFiles
      execution.setVariable('homsOrderDataFileList', JSON.to(newList))
    }
    return newFiles
  }

  def attachFile(LinkedHashMap file, Boolean save = true) {
    return attachFiles([file], save)
  }
}