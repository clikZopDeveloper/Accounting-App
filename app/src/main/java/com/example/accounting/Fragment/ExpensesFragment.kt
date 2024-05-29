package com.example.accounting.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.accounting.Activity.*
import com.example.accounting.Adapter.GetExpenseAdapter
import com.example.accounting.Adapter.GetSaleAdapter
import com.example.accounting.ApiHelper.ApiController
import com.example.accounting.ApiHelper.ApiResponseListner
import com.example.accounting.Model.*
import com.example.accounting.Utills.*
import com.google.gson.JsonElement
import com.stpl.antimatter.Utils.ApiContants

class ExpensesFragment : Fragment(), ApiResponseListner {

    private lateinit var apiClient: ApiController
    private var _binding: com.example.accounting.databinding.FragmentExpensesBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.example.accounting.databinding.FragmentExpensesBinding.inflate(
            inflater,
            container,
            false
        )

        val root: View = binding.root

        val titleText = (activity as DashboardActivity?)
        titleText?.setTitle("All Expenses")

        binding.fbAddArchitect.setOnClickListener {
            requireActivity().startActivity(Intent(requireActivity(), AddExpensesActivity::class.java).putExtra("way","Add Expenses"))
        }

        apiClient = ApiController(activity, this)
  //    ApiContants.movabalebutton(binding.fbAddArchitect,requireActivity())

        apiGetExpenses()

        return root
    }

    fun apiGetExpenses() {
        SalesApp.isAddAccessToken = true
        val params = Utility.getParmMap()
        apiClient.progressView.showLoader()
        apiClient.getApiPostCall(ApiContants.getExpenses, params)
    }

    override fun success(tag: String?, jsonElement: JsonElement) {
        try {
            apiClient.progressView.hideLoader()
            if (tag == ApiContants.getExpenses) {
                val getExpensesBean = apiClient.getConvertIntoModel<GetExpensesBean>(
                    jsonElement.toString(),
                    GetExpensesBean::class.java
                )
                if (getExpensesBean.error == false) {
                    handleExpensesList(getExpensesBean.data)
                }
            }

        } catch (e: Exception) {
            Log.d("error>>", e.localizedMessage)
        }
    }

    override fun failure(tag: String?, errorMessage: String) {
        apiClient.progressView.hideLoader()
        Utility.showSnackBar(requireActivity(), errorMessage)
    }
    fun handleExpensesList(data: List<GetExpensesBean.Data>) {
        binding.rcExpenses.layoutManager =
            LinearLayoutManager(requireContext())
        val mAllAdapter = GetExpenseAdapter(requireActivity(), data, object :
            RvStatusClickListner {
            override fun clickPos(status: String, pos: Int) {

            }
        })
        binding.rcExpenses.adapter = mAllAdapter
        mAllAdapter.notifyDataSetChanged()
        // rvMyAcFiled.isNestedScrollingEnabled = false
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        //   apiAllGet()
    }

}