package com.example.stockproject.priceData

data class Item(
    val ACC_TRDVAL: String,
    val ACC_TRDVOL: String,
    val BAS_DD: String,
    val CMPPREVDD_PRC: String,
    val FLUC_RT: String,
    val ISU_CD: String,
    val ISU_NM: String,
    val LIST_SHRS: String,
    val MKTCAP: String,
    val MKT_NM: String,
    val SECT_TP_NM: String,
    val TDD_CLSPRC: String,
    val TDD_HGPRC: String,
    val TDD_LWPRC: String,
    val TDD_OPNPRC: String
)