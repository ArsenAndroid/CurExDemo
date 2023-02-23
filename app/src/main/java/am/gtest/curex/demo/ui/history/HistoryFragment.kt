package am.gtest.curex.demo.ui.history

import am.gtest.curex.demo.R
import am.gtest.curex.demo.databinding.FragmentHistoryBinding
import am.gtest.curex.demo.utils.MyGlobals
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HistoryFragment : Fragment() {

//    private val logTag = "Log: " + javaClass.simpleName

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context?.let {
            val currency = arguments?.getString(MyGlobals.KEY_CURRENCY, "")
            val dialogMessage = it.getString(R.string.transactions_history_for, currency)
            binding.tvHistoryTitle.text = dialogMessage
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}