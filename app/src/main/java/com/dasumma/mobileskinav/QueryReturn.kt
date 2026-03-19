package com.dasumma.mobileskinav

class QueryReturn {
    lateinit var head: Head
    lateinit var results: Results

    class Head {
        lateinit var vars: List<String>
        override fun toString(): String {
            return vars.toString()
        }
    }
    class Results {
        lateinit var bindings: List<Map<String, TypeValue>>

        class TypeValue {
            lateinit var type: String
            lateinit var value: String
            override fun toString(): String {
                return "{type: ${type}, value: ${value}}"
            }
        }

        override fun toString(): String {
            return bindings.toString();
        }
    }

    override fun toString(): String {
        return "head: \n{${head}} \nresults: \n{${results}}"
    }
}
