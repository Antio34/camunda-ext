package org.camunda.latera.bss.connectors.hoper.hydra

trait Main {
  LinkedHashMap withId(def id = null) {
    if (id) {
      return [id: id]
    }
    return [:]
  }

  LinkedHashMap withParent(def parent = null) {
    if (parent) {
      return [parent: parent]
    }
    return [:]
  }

  LinkedHashMap nvlParams(LinkedHashMap input) {
    def params = [:]
    input.each { key, value ->
      if (value != null) {
        if (value == 'null' || value == 'NULL') {
          params[key] = null
        } else {
          params[key] = value
        }
      }
    }
    return params
  }

  List preparePathItems(LinkedHashMap type) {
    List result = []
    if (type.id) {
      result = [type.id] + result
    }
    if (type.plural) {
      result = [type.plural] + result
    }
    if (type.parent) {
      result = preparePathItems(type.parent) + result
    }
    return result
  }

  String preparePath(LinkedHashMap type, def id = null) {
    List result = preparePathItems(type + withId(id))
    return result.join('/')
  }
}