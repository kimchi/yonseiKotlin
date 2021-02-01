package kr.coursemos.yonsei

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.widget.Toast
import androidx.fragment.app.Fragment
import kr.coursemos.yonsei.a99util.CMDB
import kr.coursemos.yonsei.a99util.StaticClass
import org.json.JSONObject

open class A00_00Fragment : Fragment() {
    lateinit var cmdb: CMDB
    val preference by lazy{context!!.getSharedPreferences(StaticClass.KEY_SharedPreferences, Context.MODE_PRIVATE)}
    fun setEnv(key:String,value:String){

        preference.edit().putString(key,value).apply()
    }
    fun putEnv(key:String,value:String){
        setEnv(key,value)
    }

    fun getEnv(key:String,default:String=""): String {
        return preference.getString(key,default)?:""
    }



    fun setEnvBoolean(key:String,value:Boolean){
        preference.edit().putBoolean(key,value).apply()
    }
    fun putEnvBoolean(key:String,value:Boolean){
        setEnvBoolean(key,value)
    }

    fun getEnvBoolean(key:String,default:Boolean=false): Boolean {
        return preference.getBoolean(key,default)
    }

    fun setEnvInt(key:String,value:Int){
        preference.edit().putInt(key,value).apply()
    }
    fun putEnvInt(key:String,value:Int){
        setEnvInt(key,value)
    }

    fun getEnvInt(key:String,default:Int=0): Int {
        return preference.getInt(key,default)
    }

    fun removeEnv(key:String){
        preference.edit().remove(key).apply()
    }
    fun toast(msg:String,time:Int= Toast.LENGTH_SHORT){
        Toast.makeText(context,msg,time).show();
    }
    fun startActivity(cls:Class<*>, anim:Int=0){
        var intent= Intent(context,cls);
        intent.putExtra(StaticClass.param[0],this.javaClass.name)
        intent.putExtra(StaticClass.param[2],this.javaClass.name)
        intent.putExtra(StaticClass.param[1], StaticClass.NO)
        startActivity(intent)
    }

    @Throws(java.lang.Exception::class)
    protected open fun selectDB(
        startIndex: Int,
        type1: String?,
        type2: String?,
        table: String?,
        idList: java.util.ArrayList<String>,
        jsonList: java.util.ArrayList<String>
    ): Int {
        return selectDB(startIndex, type1, type2, "id", table, idList, jsonList)
    } // selectDB


    @Throws(java.lang.Exception::class)
    protected open fun selectDB(
        startIndex: Int,
        type1: String?,
        type2: String?,
        type3ID: String?,
        table: String?,
        idList: java.util.ArrayList<String>,
        jsonList: java.util.ArrayList<String>
    ): Int {
        var startIndex = startIndex
        cmdb = CMDB.open(context)
        var cursor: Cursor? = null
        var jo: JSONObject? = null
        cursor =
            cmdb.select(arrayOf(type1), arrayOf(type2), null, table)
        val index = cursor.getColumnIndex(CMDB.COLUMNjson)
        while (cursor.moveToNext()) {
            jo = JSONObject(cursor.getString(index))
            if (idList.size > startIndex) {
                idList[startIndex] = jo.getString(type3ID)
                jsonList[startIndex] = jo.toString()
            } else {
                idList.add(jo.getString(type3ID))
                jsonList.add(jo.toString())
            }
            startIndex++
        }
        return startIndex
    } // selectDB

    @Throws(java.lang.Exception::class)
    protected open fun deleteList(
        startIndex: Int,
        idList: java.util.ArrayList<String>,
        jsonList: java.util.ArrayList<String>,
        detailList: java.util.ArrayList<Boolean>?
    ) {
        while (startIndex < idList.size) {
            idList.removeAt(startIndex)
            jsonList.removeAt(startIndex)
            detailList?.removeAt(startIndex)
        }
    } // deleteList

}