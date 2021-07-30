package me.mucool.testapplication.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_call.view.*
import me.mucool.testapplication.R
import me.mucool.testapplication.bean.CallResponse

class CallAdapter constructor(private val datas: List<CallResponse.DataBean>, private val context: Context?): RecyclerView.Adapter<CallAdapter.CallViewHolder>() {

    public var clickPos :Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallViewHolder {
        return  CallViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: CallViewHolder, position: Int) {
        holder.bind(datas[position], position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    inner class CallViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_call, parent, false)){

        fun bind(callBean: CallResponse.DataBean, pos: Int) {
            itemView.tv_hallName.text = "${callBean.hallRoomName}"
            itemView.tv_duration.text = "${getCourseTime(callBean.duration)}"
            itemView.call_time.text = "${callBean.createDate}"
            if (callBean.state == 1 || callBean.state==3){
                itemView.tv_state.text = "完成服务"
                itemView.tv_state.setBackgroundResource(R.drawable.btn_out_line2)
                itemView.tv_state.setTextColor(Color.parseColor("#e05a5a"))
                itemView.tv_state.setOnClickListener {
                    if (itemClickListener != null)
                        itemClickListener.click(callBean.id)
                }
            }else if(callBean.state == 2){
                itemView.tv_state.text = "已处理"
                itemView.tv_state.setBackgroundResource(R.drawable.btn_out_line)
                itemView.tv_state.setTextColor(Color.parseColor("#ff5b9af7"))
            }
            itemView.rl_voice.setOnClickListener {
                notifyDataSetChanged()
                if (voiceClickListener!=null)
                    voiceClickListener.clickVoice(pos, callBean.voicePath, itemView)
            }
        }
    }

    private lateinit var itemClickListener : AdapterClickListener
    private lateinit var voiceClickListener: VoiceClickListener

    fun setOnAdapterClickListener(itemClickListener : AdapterClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun setOnVoiceClickListener(voiceClickListener : VoiceClickListener) {
        this.voiceClickListener = voiceClickListener
    }

    interface AdapterClickListener{
        fun click(id:String)
    }

    interface VoiceClickListener{
        fun clickVoice(pos:Int, dataUrl: String, item: View)
    }

    fun updateTime(){

    }

    fun getCourseTime(seconds: Int): String? {
        val day = seconds / (60 * 60 * 24) //换成天
        val hour = (seconds - 60 * 60 * 24 * day) / 3600 //总秒数-换算成天的秒数=剩余的秒数    剩余的秒数换算为小时
        val minute =
            (seconds - 60 * 60 * 24 * day - 3600 * hour) / 60 //总秒数-换算成天的秒数-换算成小时的秒数=剩余的秒数    剩余的秒数换算为分
        val second =
            seconds - 60 * 60 * 24 * day - 3600 * hour - 60 * minute //总秒数-换算成天的秒数-换算成小时的秒数-换算为分的秒数=剩余的秒数
        //System.out.println(day+"天"+hour+"时"+minute+"分"+second+"秒");
        return if (hour != 0) {
            (if (hour >= 10) hour else "0$hour").toString() + ":" + (if (minute >= 10) minute else "0$minute") + ":" + if (second >= 10) second else "0$second"
        } else if (minute != 0) {
            (if (minute >= 10) minute else "0$minute").toString() + ":" + if (second >= 10) second else "0$second"
        } else {
            "00:" + if (second >= 10) second else "0$second"
        }
    }
}