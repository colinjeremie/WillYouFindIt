package com.github.colinjeremie.willyoufindit.adapters

import android.support.annotation.VisibleForTesting
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.deezer.sdk.model.Radio
import com.deezer.sdk.network.request.event.JsonRequestListener
import com.github.colinjeremie.willyoufindit.MyApplication
import com.github.colinjeremie.willyoufindit.R
import com.github.colinjeremie.willyoufindit.utils.normalize
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RadioAdapter(private val onRadioClickListener: ((Radio) -> Unit), private val loadingCallback: (Boolean) -> Unit) : RecyclerView.Adapter<RadioAdapter.RadioViewHolder>() {
    @VisibleForTesting
    var originalDataSet: MutableList<Radio> = mutableListOf()

    private var dataSet: MutableList<Radio> = mutableListOf()

    private val fetchRadiosListener: JsonRequestListener = object : JsonRequestListener() {

        override fun onResult(o: Any, o1: Any) {
            originalDataSet = (o as List<Radio>).distinctBy { it.id }.toMutableList()
            clearFilter()
            loadingCallback.invoke(false)
        }

        override fun onUnparsedResult(s: String, o: Any) {
            loadingCallback.invoke(false)
        }

        override fun onException(e: Exception, o: Any) {
            loadingCallback.invoke(false)
        }
    }

    fun fetchRadios() {
        loadingCallback.invoke(true)
        MyApplication.instance.deezerApi.getRadios(fetchRadiosListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RadioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.radio_item, parent, false)

        return RadioViewHolder(view)
    }

    override fun onBindViewHolder(holder: RadioViewHolder, position: Int) {
        val model = dataSet[position]

        holder.name.text = model.title
        Glide
                .with(holder.image.context)
                .load(model.pictureUrl)
                .into(holder.image)

        holder.itemView.setOnClickListener { onRadioClickListener?.invoke(model) }
    }

    fun clearFilter() {
        dataSet.clear()
        dataSet.addAll(originalDataSet)
        notifyDataSetChanged()
    }

    override fun getItemCount() = dataSet.size

    fun filter(search: String) {
        getFilterObservable(search, originalDataSet)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    dataSet = result
                    notifyDataSetChanged()
                }
    }

    fun getFilterObservable(search: String, list: List<Radio>) =
            Observable
                    .fromIterable(list)
                    .filter { it.title.normalize().contains(search.normalize(), ignoreCase = true) }
                    .toList()

    /**
     * View holder for a [Radio] item
     */
    class RadioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById<View>(R.id.radio_picture) as ImageView
        val name: TextView = itemView.findViewById<View>(R.id.radio_name) as TextView
    }
}
