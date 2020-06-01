package com.lijinjiliangcha.moveconstraintlayout.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val adapter: TestAdapter by lazy { TestAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAdapter()
        initListener()

    }

    private fun initAdapter() {
        rv.adapter = adapter
    }

    private fun initListener() {
        tv_move.setOnClickListener {
            Toast.makeText(this, "点击", Toast.LENGTH_SHORT).show()
        }

//        tv_move_1.setOnClickListener {
//            Toast.makeText(this, "点击", Toast.LENGTH_SHORT).show()
//        }

        srl.setOnRefreshLoadMoreListener(object : OnRefreshLoadMoreListener {
            override fun onLoadMore(refreshLayout: RefreshLayout) {
                adapter.add()
                srl.finishLoadMore()
            }

            override fun onRefresh(refreshLayout: RefreshLayout) {
                adapter.reset()
                srl.finishRefresh()
            }

        })
    }

}
