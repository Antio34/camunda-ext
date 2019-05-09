package org.camunda.latera.bss.connectors

import org.camunda.latera.bss.http.HTTPRestProcessor
import org.camunda.latera.bss.logging.SimpleLogger
import org.camunda.latera.bss.utils.Base64Converter
import org.camunda.bpm.engine.delegate.DelegateExecution

class Hoper {
  String url
  String user
  private String password
  Integer version
  HTTPRestProcessor http
  SimpleLogger logger

  Hoper(DelegateExecution execution) {
    this.logger    = new SimpleLogger(execution)

    this.url      = execution.getVariable("hoperUrl")     ?: 'http://hoper:3000'
    this.version  = execution.getVariable("hoperVersion") ?: 2
    this.user     = execution.getVariable("hydraUser")
    this.password = execution.getVariable("hydraPassword")
    this.http     = new HTTPRestProcessor(
      baseUrl   : this.url,
      execution : execution
    )
  }

  private def authToken() {
    def auth = [
      session: [
        login    : this.user,
        password : this.password
      ]
    ]
    return http.sendRequest(
      'get',
      path : "/rest/v${this.version}/login",
      body : auth,
      supressRequestBodyLog  : true,
      supressResponseBodyLog : true
    )?.session?.token
  }

  private def authBasic() {
    def auth = "${this.user}:${this.password}"
    return Base64Converter.to(auth)
  }

  private def authHeader() {
    if (this.version == 1) {
      return ['Authorization': "Basic ${this.authBasic()}"]
    }
    if (this.version == 2) {
      return ['Authorization': "Token token=\"${this.authToken()}\""]
    }
    return []
  }

  def sendRequest(LinkedHashMap input, String method = 'get') {
    if (!input.headers) {
      input.headers = [:]
    }
    input.headers += this.authHeader()
    input.path = "/rest/v${this.version}/${input.path}".toString()
    return http.sendRequest(input, method)
  }
}