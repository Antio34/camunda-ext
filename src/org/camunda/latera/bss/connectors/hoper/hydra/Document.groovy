package org.camunda.latera.bss.connectors.hoper.hydra

trait Document {
  private static Integer DOCUMENT_STATE_ACTUAL_ID      = 4003 // 'DOC_STATE_Actual'
  private static Integer DOCUMENT_STATE_EXECUTED_ID    = 6003 // 'DOC_STATE_Executed'
  private static Integer DOCUMENT_STATE_DRAFT_ID       = 3003 // 'DOC_STATE_Draft'
  private static Integer DOCUMENT_STATE_CANCELED_ID    = 5003 // 'DOC_STATE_Canceled'
  private static Integer DOCUMENT_STATE_CLOSED_ID      = 9003 // 'DOC_STATE_Closed'
  private static Integer DOCUMENT_STATE_DISSOLVED_ID   = 12003 // 'DOC_STATE_Dissolved'
  private static Integer DOCUMENT_STATE_PROCESSING_ID  = 11003 // 'DOC_STATE_Processing'
  private static Integer DOCUMENT_STATE_PREPARED_ID    = 10003 // 'DOC_STATE_Prepared'
  private static Integer PROVIDER_ROLE_ID = 2004 // 'SUBJ_ROLE_Provider'
  private static Integer RECEIVER_ROLE_ID = 1004 // 'SUBJ_ROLE_Receiver'

  def getDocumentStateActualId() {
    return DOCUMENT_STATE_ACTUAL_ID
  }

  def getDocumentStateExecutedId() {
    return DOCUMENT_STATE_EXECUTED_ID
  }

  def getDocumentStateDraftId() {
    return DOCUMENT_STATE_DRAFT_ID
  }

  def getDocumentStateCanceledId() {
    return DOCUMENT_STATE_CANCELED_ID
  }

  def getDocumentStateClosedId() {
    return DOCUMENT_STATE_CLOSED_ID
  }

  def getDocumentStateDissolvedId() {
    return DOCUMENT_STATE_DISSOLVED_ID
  }

  def getDocumentStateProcessingId() {
    return DOCUMENT_STATE_PROCESSING_ID
  }

  def getDocumentStatePreparedId() {
    return DOCUMENT_STATE_PREPARED_ID
  }

  def getProviderRoleId() {
    return PROVIDER_ROLE_ID
  }

  def getReceiverRoleId() {
    return RECEIVER_ROLE_ID
  }
}