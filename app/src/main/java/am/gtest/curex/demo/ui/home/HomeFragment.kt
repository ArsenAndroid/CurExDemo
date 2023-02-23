package am.gtest.curex.demo.ui.home

import am.gtest.curex.demo.R
import am.gtest.curex.demo.data.model.BalanceModel
import am.gtest.curex.demo.data.model.ExchangeModel
import am.gtest.curex.demo.data.model.RateModel
import am.gtest.curex.demo.data.remote.response.MyResponse
import am.gtest.curex.demo.databinding.FragmentHomeBinding
import am.gtest.curex.demo.ui.MainViewModel
import am.gtest.curex.demo.utils.MyGlobals
import am.gtest.curex.demo.utils.MyPrefs
import am.gtest.curex.demo.utils.MyUtils
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.badge.ExperimentalBadgeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.roundToInt

@AndroidEntryPoint
@ExperimentalBadgeUtils
class HomeFragment : Fragment() {

//    private val logTag = "Log: " + javaClass.simpleName

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<MainViewModel>()

    private var job: Job? = null

    private lateinit var balancesRvAdapter: BalancesRvAdapter

    var rates = listOf<RateModel>()
    var balances = listOf<BalanceModel>()

    private lateinit var titlesAdapter: ArrayAdapter<String>

    @Inject
    @Named(MyPrefs.PREF_FILE_CURRENT_USER)
    lateinit var myPrefsCurrentUser: SharedPreferences

    private var refreshReceiveAmountJob: Job? = null
    private var refreshSellAmountJob: Job? = null

    private val sellAmountTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(text: Editable?) {
            text?.let {
                if (it.length > 1 && it.startsWith('0') && !it.startsWith("0.")) {
                    binding.etSellAmount.setText(it.substring(1, it.length))
                    binding.etSellAmount.setSelection(binding.etSellAmount.text.length)
                }
            }

            refreshReceiveAmountJob?.cancel()
            refreshReceiveAmountJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(300L)
                refreshReceiveAmount()
            }
        }
    }

    private val receiveAmountTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(text: Editable?) {
            text?.let {
                if (it.length > 1 && it.startsWith('0') && !it.startsWith("0.")) {
                    binding.etReceiveAmount.setText(it.substring(1, it.length))
                    binding.etReceiveAmount.setSelection(binding.etReceiveAmount.text.length)
                }
            }

            refreshSellAmountJob?.cancel()
            refreshSellAmountJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(300L)
                refreshSellAmount()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when MyFragment is at least Started.
        activity?.let {
            it.onBackPressedDispatcher.addCallback(this) {
                AlertDialog.Builder(it)
                    .setMessage(R.string.exit_app_warning)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.exit) { _: DialogInterface?, _: Int ->
                        activity?.finish()
                    }
                    .show()
            }
        }
        if (!myPrefsCurrentUser.getBoolean(MyPrefs.INITIAL_BALANCE_INSERTED, false)) {
            viewModel.insertInitialBalance()
            myPrefsCurrentUser.edit().putBoolean(MyPrefs.INITIAL_BALANCE_INSERTED, true).apply()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        balancesRvAdapter = BalancesRvAdapter()

        balancesRvAdapter.clickListener = {
            val bundle = bundleOf(
                MyGlobals.KEY_CURRENCY to it,
            )
            findNavController().navigate(R.id.action_nav_home_to_nav_history, bundle)
        }

        binding.rvBalances.adapter = balancesRvAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getLocalRates().collectLatest { rateModels ->
                delay(200)
                rates = rateModels
                setBalances()
                titlesAdapter.clear()
                titlesAdapter.addAll(rates.map { it.currency })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getLocalBalances().collectLatest {
                delay(200)
                balances = it
                setBalances()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.exchangeChannelFlow.collectLatest { myResponse ->
                when (myResponse) {
                    is MyResponse.Success -> {

                        val exchangeModel = myResponse.data

                        if (exchangeModel != null) {

                            var transactionsCount = myPrefsCurrentUser.getInt(MyPrefs.TRANSACTIONS_COUNT, 0)
                            transactionsCount++
                            myPrefsCurrentUser.edit().putInt(MyPrefs.TRANSACTIONS_COUNT, transactionsCount).apply()

                            binding.etSellAmount.removeTextChangedListener(sellAmountTextWatcher)
                            binding.etSellAmount.setText("")
                            binding.etSellAmount.addTextChangedListener(sellAmountTextWatcher)

                            binding.etReceiveAmount.removeTextChangedListener(receiveAmountTextWatcher)
                            binding.etReceiveAmount.setText("")
                            binding.etReceiveAmount.addTextChangedListener(receiveAmountTextWatcher)

                            context?.let {
                                val dialogMessage = it.getString(
                                    R.string.exchange_result,
                                    exchangeModel.sellAmount.toString(),
                                    exchangeModel.sellCurrency,
                                    exchangeModel.receiveAmount.toString(),
                                    exchangeModel.receiveCurrency,
                                    exchangeModel.fees.toString(),
                                    exchangeModel.sellCurrency
                                )
                                AlertDialog.Builder(it)
                                    .setTitle(R.string.currency_converted)
                                    .setMessage(dialogMessage)
                                    .setPositiveButton(R.string.ok, null)
                                    .show()
                            }
                        } else {
                            context?.let {
                                Toast.makeText(it, R.string.something_went_wrong_try_again, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    is MyResponse.Error -> {
                        context?.let {
                            Toast.makeText(it, R.string.something_went_wrong_try_again, Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> Unit
                }
            }
        }

        setupCurrenciesExchange()

        binding.btnSubmit.setOnClickListener {
            checkThenDoExchange()
        }
    }

    override fun onResume() {
        super.onResume()

        stopUpdates()

        job = viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                viewModel.getRemoteRates()
                delay(5000)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopUpdates()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun stopUpdates() {
        job?.cancel()
        job = null
    }

    private fun setBalances() {

        if (rates.isNotEmpty() && balances.isNotEmpty()) {

            val updatedBalances = mutableListOf<BalanceModel>()

            for (rate in rates) {

                val updatedBalance = BalanceModel(currency = rate.currency)

                for (balance in balances) {
                    if (rate.currency == balance.currency) {
                        updatedBalance.balance = balance.balance
                        break
                    }
                }

                updatedBalances.add(updatedBalance)
            }

            updatedBalances.sortBy { it.currency }

            balancesRvAdapter.setBalances(updatedBalances)
        }
    }

    private fun setupCurrenciesExchange() {

        binding.etSellCurrency.inputType = EditorInfo.TYPE_NULL
        binding.etReceiveCurrency.inputType = EditorInfo.TYPE_NULL

        binding.etSellCurrency.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                MyUtils.hideKeypad(view)
            }
        }

        binding.etReceiveCurrency.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                MyUtils.hideKeypad(view)
            }
        }

        binding.etSellCurrency.addTextChangedListener {
            refreshReceiveAmount()
        }

        binding.etReceiveCurrency.addTextChangedListener {
            refreshSellAmount()
        }

        binding.etSellAmount.addTextChangedListener(sellAmountTextWatcher)

        binding.etReceiveAmount.addTextChangedListener(receiveAmountTextWatcher)

        context?.let { ctx ->
            titlesAdapter = ArrayAdapter(ctx, R.layout.drop_down, rates.map { it.currency })
            binding.etSellCurrency.setAdapter(titlesAdapter)
            binding.etReceiveCurrency.setAdapter(titlesAdapter)
        }
    }

    private fun refreshReceiveAmount() {

        val sellCurrency = binding.etSellCurrency.text.toString().trim()
        val receiveCurrency = binding.etReceiveCurrency.text.toString().trim()
        val sellAmountString = binding.etSellAmount.text.toString().trim()

        if (sellCurrency.isEmpty() || !rates.map { it.currency }.contains(sellCurrency)) {
            return
        }

        if (receiveCurrency.isEmpty() || !rates.map { it.currency }.contains(receiveCurrency)) {
            return
        }

        if (sellAmountString.isEmpty()) {
            return
        }

        val sellAmount = try {
            sellAmountString.toDouble()
        } catch (e: Exception) {
            -1.0
        }

        if (sellAmount >= 0) {
            val sellToEurRate = try {
                rates.filter { it.currency == sellCurrency }[0].rate
            } catch (e: Exception) {
                -1.0
            }

            if (sellToEurRate <= 0) {
                return
            }

            val receiveToEurRate = try {
                rates.filter { it.currency == receiveCurrency }[0].rate
            } catch (e: Exception) {
                -1.0
            }

            if (receiveToEurRate <= 0) {
                return
            }

            val receiveAmount = sellAmount / sellToEurRate * receiveToEurRate
            val roundedAmount = String.format("%.3f", receiveAmount)

            binding.etReceiveAmount.removeTextChangedListener(receiveAmountTextWatcher)
            binding.etReceiveAmount.setText(roundedAmount)
            binding.etReceiveAmount.addTextChangedListener(receiveAmountTextWatcher)
        }
    }

    private fun refreshSellAmount() {

        val sellCurrency = binding.etSellCurrency.text.toString().trim()
        val receiveCurrency = binding.etReceiveCurrency.text.toString().trim()
        val receiveAmountString = binding.etReceiveAmount.text.toString().trim()

        if (sellCurrency.isEmpty() || !rates.map { it.currency }.contains(sellCurrency)) {
            return
        }

        if (receiveCurrency.isEmpty() || !rates.map { it.currency }.contains(receiveCurrency)) {
            return
        }

        if (receiveAmountString.isEmpty()) {
            return
        }

        val receiveAmount = try {
            receiveAmountString.toDouble()
        } catch (e: Exception) {
            -1.0
        }

        if (receiveAmount >= 0) {
            val sellToEurRate = try {
                rates.filter { it.currency == sellCurrency }[0].rate
            } catch (e: Exception) {
                -1.0
            }

            if (sellToEurRate <= 0) {
                return
            }

            val receiveToEurRate = try {
                rates.filter { it.currency == receiveCurrency }[0].rate
            } catch (e: Exception) {
                -1.0
            }

            if (receiveToEurRate <= 0) {
                return
            }

            val sellAmount = receiveAmount / receiveToEurRate * sellToEurRate
            val roundedAmount = String.format("%.3f", sellAmount)

            binding.etSellAmount.removeTextChangedListener(sellAmountTextWatcher)
            binding.etSellAmount.setText(roundedAmount)
            binding.etSellAmount.addTextChangedListener(sellAmountTextWatcher)
        }
    }

    private fun checkThenDoExchange() {
        val sellCurrency = binding.etSellCurrency.text.toString().trim()
        val receiveCurrency = binding.etReceiveCurrency.text.toString().trim()
        val sellAmountString = binding.etSellAmount.text.toString().trim()
        val receiveAmountString = binding.etReceiveAmount.text.toString().trim()

        if (sellCurrency.isEmpty()) {
            Toast.makeText(requireContext(), R.string.please_select_selling_currency, Toast.LENGTH_SHORT).show()
            return
        }

        if (!rates.map { it.currency }.contains(sellCurrency)) {
            Toast.makeText(requireContext(), R.string.selling_currency_not_available, Toast.LENGTH_SHORT).show()
            return
        }

        if (receiveCurrency.isEmpty()) {
            Toast.makeText(requireContext(), R.string.please_select_receiving_currency, Toast.LENGTH_SHORT).show()
            return
        }

        if (!rates.map { it.currency }.contains(receiveCurrency)) {
            Toast.makeText(requireContext(), R.string.receiving_currency_not_available, Toast.LENGTH_SHORT).show()
            return
        }

        if (sellCurrency == receiveCurrency) {
            Toast.makeText(requireContext(), R.string.please_select_different_currencies, Toast.LENGTH_SHORT).show()
            return
        }

        val sellAmount = try {
            sellAmountString.toDouble()
        } catch (e: Exception) {
            -1.0
        }

        if (sellAmount <= 0) {
            Toast.makeText(requireContext(), R.string.selling_amount_must_be_more_than_zero, Toast.LENGTH_SHORT).show()
            return
        }

        if (sellAmount < MyGlobals.MINIMUM_TRADING_AMOUNT) {
            Toast.makeText(requireContext(), R.string.small_selling_amount, Toast.LENGTH_SHORT).show()
            return
        }

        val sellBalance = try {
            balances.filter { it.currency == sellCurrency }[0].balance
        } catch (e: Exception) {
            -1.0
        }

        if (sellAmount > sellBalance) {
            Toast.makeText(requireContext(), R.string.you_don_t_have_enough_balance_to_exchange, Toast.LENGTH_SHORT).show()
            return
        }

        val receiveAmount = try {
            receiveAmountString.toDouble()
        } catch (e: Exception) {
            -1.0
        }

        if (receiveAmount < MyGlobals.MINIMUM_TRADING_AMOUNT) {
            Toast.makeText(requireContext(), R.string.small_receiving_amount, Toast.LENGTH_SHORT).show()
            return
        }

        val receiveBalance = try {
            balances.filter { it.currency == receiveCurrency }[0].balance
        } catch (e: Exception) {
            0.0
        }

        var fees = 0.0
        val transactionsCount = myPrefsCurrentUser.getInt(MyPrefs.TRANSACTIONS_COUNT, 0)

        if (transactionsCount >= MyGlobals.FREE_TRANSACTIONS_COUNT) {
            fees = sellAmount * MyGlobals.FEES_PERCENT / 100
            fees = (fees * 1000.0).roundToInt() / 1000.0
            if (sellAmount > sellBalance - fees) {
                Toast.makeText(requireContext(), R.string.you_don_t_have_enough_balance_for_amount_and_fees, Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }

        val exchangeModel = ExchangeModel(
            sellCurrency = sellCurrency,
            sellBalance = sellBalance,
            sellAmount = sellAmount,
            receiveCurrency = receiveCurrency,
            receiveBalance = receiveBalance,
            receiveAmount = receiveAmount,
            fees = fees,
        )

        MyUtils.hideKeypad(view)

        viewModel.doExchange(exchangeModel)
    }
}