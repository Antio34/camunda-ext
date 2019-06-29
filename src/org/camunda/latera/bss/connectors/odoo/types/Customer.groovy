package org.camunda.latera.bss.connectors.odoo.types

trait Customer {
  private static String CUSTOMER_ENTITY_TYPE = 'res.partner'

  String getCustomerEntityType() {
    return CUSTOMER_ENTITY_TYPE
  }

  Map getCustomerDefaultParams() {
    return [
      name            : null,
      email           : null,
      isCompany       : null,
      companyName     : null,
      organizationId  : null,
      userId          : null,
      teamId          : null,
      phoneNumber     : null,
      countryId       : null,
      stateId         : null,
      city            : null,
      street          : null,
      street2         : null,
      zip             : null,
      hydraCustomerId : null,
      comment         : null
    ]
  }

  Map getCustomerParamsMap(Map params) {
    return [
      name          : params.name,
      email         : params.email,
      is_company    : params.isCompany,
      company_name  : params.companyName,
      company_id    : params.organizationId,
      user_id       : params.userId,
      team_id       : params.teamId,
      phone         : params.phoneNumber,
      country_id    : params.countryId,
      state_id      : params.stateId,
      city          : params.city,
      street        : params.street,
      street2       : params.street2,
      zip           : params.zip,
      hydra_account : params.hydraCustomerId,
      comment       : params.comment,
      customer      : true
    ]
  }

  Map getCustomerParams(Map input, Map additionalParams = [:]) {
    LinkedHashMap params = getCustomerDefaultParams() + input
    return prepareParams(this.&getCustomerParamsMap, params, additionalParams)
  }

  Map getCustomer(def id) {
    return getEntity(getCustomerEntityType(), id)
  }

  List getCustomersBy(Map input, Map additionalParams = [:]) {
    LinkedHashMap params = getCustomerParams(input, additionalParams)
    return getEntitiesBy(getCustomerEntityType(), params)
  }

  Map getCustomerBy(Map input, Map additionalParams = [:]) {
    return getCustomersBy(input, additionalParams)?.getAt(0)
  }

  Map createCustomer(Map input, Map additionalParams = [:]) {
    LinkedHashMap params = getCustomerParams(input, additionalParams)
    return createEntity(getCustomerEntityType(), params)
  }

  Map updateCustomer(def id, Map input, Map additionalParams = [:]) {
    LinkedHashMap params = getCustomerParams(input, additionalParams)
    return updateEntity(getCustomerEntityType(), id, params)
  }

  Map updateCustomer(Map input, Map additionalParams = [:]) {
    def id = input.id ?: input.customerId
    input.remove('id')
    input.remove('customerId')
    return updateCustomer(id, input, additionalParams)
  }

  Map updateCustomer(Map input, def id, Map additionalParams = [:]) {
    return updateCustomer(id, input, additionalParams)
  }

  Boolean deleteCustomer(def id) {
    return deleteEntity(getCustomerEntityType(), id)
  }
}