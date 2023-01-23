package am.gtest.curex.demo.ui.home

import am.gtest.curex.demo.data.model.BalanceModel
import am.gtest.curex.demo.databinding.ItemBalanceBinding
import am.gtest.curex.demo.utils.setCustomMargins
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("NotifyDataSetChanged")
class BalancesRvAdapter : RecyclerView.Adapter<BalancesRvAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemBalanceBinding) : RecyclerView.ViewHolder(binding.root)

//    private val logTag = "Log: " + javaClass.simpleName

    private lateinit var ctx: Context

    private val balances = mutableListOf<BalanceModel>()

    fun setBalances(balances: List<BalanceModel>) {
        this.balances.clear()
        this.balances.addAll(balances)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return balances.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        ctx = parent.context
        val binding = ItemBalanceBinding.inflate(LayoutInflater.from(ctx), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val binding = holder.binding
        val link = balances[position]

        binding.tvCurrency.text = link.currency
        binding.tvBalance.text = link.balance.toString()

        holder.itemView.setCustomMargins(ctx, position, itemCount, 32, 8, 32, 8, 8)
    }
}