package kr.coursemos.yonsei

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kr.co.okins.kc.A00_00Activity
import kr.coursemos.yonsei.a99util.CMCommunication
import kr.coursemos.yonsei.a99util.Env
import kr.coursemos.myapplication.a99util.EnvError
import java.util.*

class A00_01Login : A00_00Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_a00_01_login)
        super.onCreate(savedInstanceState)
        try{

        }catch (e:Exception){
            Env.error(e)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
//				isExit = true;
//				confirm("종료");
                val now = System.currentTimeMillis()
                if (0 < exitTime && exitTime > now - 3000) {
                    finish()
                } else {
                    exitTime = now
                    toast(getString(R.string.alert_exit))
                }
                return true
            }
        } catch (e: Exception) {
            Env.error("", e)
        }
        return super.onKeyDown(keyCode, event)
    }
    val adapter=thisAdapter()
    inner class thisAdapter() : BaseAdapter(){
        public var idList: ArrayList<String> = ArrayList()
        public var jsonList: ArrayList<String> = ArrayList()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            lateinit var layout:View
            try{
                LayoutInflater.from(applicationContext).inflate(R.layout.list_a01_03subjectheader,null,false)
            }catch (e:Exception){
                EnvError(e)
            }

            return  layout
        }

        override fun getItem(position: Int): Any {
            return 0

        }

        override fun getItemId(position: Int): Long {

            return 1
        }

        override fun getCount(): Int {
            return idList.size
        }

    }

    override fun success(what: String?, jyc: CMCommunication?) {
        try {
            if (jyc == null) {
                val id = what!!.toInt()
                if (id == ID_alertOK) {
                } else if (id == ID_confirmOK) {
                } else if (id == ID_confirmCancel) {
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }
//    inner class AutoCompleteDogsAdapter(context: Context?, dogs: List<String>) : ArrayAdapter<String?>(context!!, 0, dogs) {
//
//        override fun getCount(): Int {
//            return 3
//        }
//
//        override fun getFilter(): Filter {
//            return DogsFilter(context, this, dogs)
//        }
//
//        override fun getView( position: Int,convertView: View?, parent: ViewGroup): View {
//
//            return layoutView
//        }
//
//
//
//    }

//    inner class DogsFilter(
//        var context: Context,
//        var adapter: AutoCompleteDogsAdapter,
//        var originalList: List<String>
//    ) :
//        Filter() {
//        override fun performFiltering(constraint: CharSequence): FilterResults {
//            val results = FilterResults()
//            results.values = originalList
//            results.count = originalList.size
//            return results
//        }
//
//        override fun publishResults(
//            constraint: CharSequence,
//            results: FilterResults
//        ) {
//            results.values = originalList
//            results.count = originalList.size
//            adapter.notifyDataSetChanged()
//        }
//
//    }
}