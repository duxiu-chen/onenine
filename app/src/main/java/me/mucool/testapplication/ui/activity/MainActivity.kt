package me.mucool.testapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.acitvity_main.*
import kotlinx.android.synthetic.main.activity_service_record.*
import me.mucool.testapplication.R
import me.mucool.testapplication.ui.base.BaseActivity


class MainActivity : BaseActivity() {

    private lateinit var home: ServiceRecordFragment
    private lateinit var person: UserFragment
    private lateinit var fragments: Array<Fragment>
    private var lastfragment = 0 //用于记录上个选择的Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        home = ServiceRecordFragment()
        person = UserFragment()
        fragments = arrayOf(home, person)
        supportFragmentManager.beginTransaction().replace(R.id.mainView,home).show(home).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.menu_home ->{
                    if(lastfragment!=0) {
                        switchFragment(lastfragment,0);
                        lastfragment=0;

                    }
                }
                R.id.menu_person ->{
                    if(lastfragment!=1)
                    {
                        switchFragment(lastfragment,1);
                        lastfragment=1;
                    }
                }
            }
            return@setOnNavigationItemSelectedListener true
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.acitvity_main
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*然后在碎片中调用重写的onActivityResult方法*/
        person.onActivityResult(requestCode, resultCode, data)
    }

    //切换Fragment
    private fun switchFragment(lastfragment: Int, index: Int) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.hide(fragments[lastfragment]) //隐藏上个Fragment
        if (fragments[index].isAdded === false) {
            transaction.add(R.id.mainView, fragments[index])
        }
        transaction.show(fragments[index]).commitAllowingStateLoss()
    }





}