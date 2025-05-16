package malibu.multiturn.framework.config

import com.fasterxml.jackson.databind.ObjectMapper

interface MappingFunction {
    fun mapping(om: ObjectMapper)
}