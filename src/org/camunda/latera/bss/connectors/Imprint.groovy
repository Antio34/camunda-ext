package org.camunda.latera.bss.connectors

import org.camunda.latera.bss.http.HTTPRestProcessor
import org.camunda.latera.bss.logging.SimpleLogger
import static org.camunda.latera.bss.utils.DateTimeUtil.*
import org.camunda.latera.bss.utils.Order
import org.camunda.bpm.engine.delegate.DelegateExecution

class Imprint {
  String url
  String version
  private String token
  HTTPRestProcessor http
  SimpleLogger logger
  String locale
  DelegateExecution execution

  Imprint(DelegateExecution execution) {
    this.execution  = execution
    this.logger     = new SimpleLogger(execution)
    def ENV         = System.getenv()

    this.locale     = execution.getVariable('locale') ?: 'ru'
    this.url        =  ENV['IMPRINT_URL']     ?: execution.getVariable('imprintUrl')     ?: 'http://imprint:2300/api'
    this.version    = (ENV['IMPRINT_VERSION'] ?: execution.getVariable('imprintVersion') ?: 1)?.toInteger()
    this.token      =  ENV['IMPRINT_TOKEN']   ?: execution.getVariable('imprintToken')
    LinkedHashMap headers = [
      'X_IMPRINT_API_VERSION' : this.version,
      'X_IMPRINT_API_TOKEN'   : this.token
    ]
    this.http = new HTTPRestProcessor(
      baseUrl   : url,
      headers   : headers,
      execution : execution,
      supressResponseBodyLog : true
    )
  }

  def print(CharSequence template, Map data = [:]) {
    this.logger.info('Printing begin...')
    if (!data) {
      data = Order.getData(execution)
    }
    def now = local()
    LinkedHashMap body = [
      template : template,
      data     : [
                  nowISO        : now,
                  now           : format(now),
                  today         : format(now, SIMPLE_DATE_FORMAT),
                  day           : format(now, DAY_FORMAT),
                  month         : format(now, MONTH_FORMAT),
                  year          : format(now, YEAR_FORMAT),
                  monthFull     : format(now, MONTH_FULL_FORMAT, this.locale),
                  todayFull     : format(now, FULL_DATE_FORMAT,  this.locale)
                ] + data
    ]
    return this.http.sendRequest(
      'post',
      path : 'print',
      body : body
    )
  }

  def print(Map data, CharSequence template) {
    return this.print(template, data)
  }
}