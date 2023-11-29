package duc.thanhhoa.chatduck.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import duc.thanhhoa.adapter.UserAdapter
import duc.thanhhoa.chatduck.R
import duc.thanhhoa.chatduck.databinding.FragmentHomeBinding
import duc.thanhhoa.chatduck.mvvm.ChatAppViewModel


class HomeFragment : Fragment() {
    lateinit var rv: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var userViewModel: ChatAppViewModel
    lateinit var homeBinding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding= DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel= ViewModelProvider(this).get(ChatAppViewModel::class.java)
        userAdapter= UserAdapter()
        rv= homeBinding.rvUsers

        val layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)

        rv.layoutManager= layoutManager

        userViewModel.getUsers().observe(viewLifecycleOwner,{

            userAdapter.setUserList(it)
            rv.adapter= userAdapter
        })
    }


}