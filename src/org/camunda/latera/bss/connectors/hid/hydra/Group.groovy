package org.camunda.latera.bss.connectors.hid.hydra

trait Group {
  private static String GROUPS_TABLE = 'SI_V_SUBJ_GROUPS'
  private static String GROUP_TYPE   = 'SUBJ_TYPE_Group'

  String getGroupsTable() {
    return GROUPS_TABLE
  }

  String getGroupType() {
    return GROUP_TYPE
  }

  Number getGroupTypeId() {
    return getRefIdByCode(getGroupType())
  }

  Map getGroup(def groupId) {
    LinkedHashMap where = [
      n_subject_id: groupId
    ]
    return hid.getTableFirst(getGroupsTable(), where: where)
  }

  List getGroupsBy(Map input) {
    LinkedHashMap params = mergeParams([
      groupId       : null,
      subjectTypeId : null,
      creatorId     : null,
      name          : null,
      code          : null,
      firmId        : getFirmId(),
      stateId       : getSubjectStateOnId()
    ], input)
    LinkedHashMap where = [:]

    if (params.groupId) {
      where.n_subject_id = params.groupId
    }
    if (params.subjectTypeId) {
      where.n_grp_subj_type_id = params.subjectTypeId
    }
    if (params.creatorId) {
      where.n_creator_id = params.creatorId
    }
    if (params.name) {
      where.vc_name = params.name
    }
    if (params.code) {
      where.vc_code = params.code
    }
    if (params.groupId) {
      where.n_subj_group_id = params.groupId
    }
    if (params.resellerId) {
      where.n_reseller_id = params.resellerId
    }
    if (params.stateId) {
      where.n_subj_state_id = params.stateId
    }
    return hid.getTableData(getGroupsTable(), where: where)
  }

  Map getGroupBy(Map input) {
    return getGroupsBy(input)?.getAt(0)
  }

  Boolean isGroup(CharSequence entityType) {
    return entityType == getGroupType()
  }

  Boolean isGroup(def entityIdOrEntityTypeId) {
    return entityIdOrEntityTypeId == getGroupTypeId() || getGroup(entityIdOrEntityTypeId) != null
  }
}