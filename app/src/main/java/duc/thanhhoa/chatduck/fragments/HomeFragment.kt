package duc.thanhhoa.chatduck.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import duc.thanhhoa.chatduck.adapter.UserAdapter
import duc.thanhhoa.chatduck.R
import duc.thanhhoa.chatduck.SignInActivity
import duc.thanhhoa.chatduck.adapter.OnItemClickListener
import duc.thanhhoa.chatduck.adapter.OnRecentChatListener
import duc.thanhhoa.chatduck.adapter.RecentChatAdapter
import duc.thanhhoa.chatduck.databinding.FragmentHomeBinding
import duc.thanhhoa.chatduck.modal.RecentChats
import duc.thanhhoa.chatduck.modal.Users
import duc.thanhhoa.chatduck.mvvm.ChatAppViewModel


class HomeFragment : Fragment() , OnItemClickListener, OnRecentChatListener{
    lateinit var rv: RecyclerView
    lateinit var userAdapter: UserAdapter
    lateinit var userViewModel: ChatAppViewModel
    lateinit var homeBinding: FragmentHomeBinding
    lateinit var  fbauth: FirebaseAuth
    lateinit var toolbar: Toolbar
    lateinit var circleImageView: CircleImageView
    lateinit var recentChatAdapter: RecentChatAdapter
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

        fbauth= FirebaseAuth.getInstance()

        toolbar= view.findViewById(R.id.toolbarMain)
        circleImageView= toolbar.findViewById(R.id.tlImage)

        homeBinding.lifecycleOwner = viewLifecycleOwner

        userAdapter= UserAdapter()
        rv= homeBinding.rvUsers

        val layoutManager= LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)

        rv.layoutManager= layoutManager

        userViewModel.getUsers().observe(viewLifecycleOwner, Observer{

            userAdapter.setUserList(it)

            rv.adapter= userAdapter
        })

        userAdapter.setOnClickListener(this)

        homeBinding.logOut.setOnClickListener{
            fbauth.signOut()
            startActivity(Intent(requireContext(), SignInActivity::class.java))
        }

        userViewModel.imageUrl.observe(viewLifecycleOwner, Observer {
            Glide.with(requireContext())
                .load(it)
                .into(circleImageView)
        })

        recentChatAdapter= RecentChatAdapter()

        userViewModel.getRecentChat().observe(viewLifecycleOwner, Observer {

            homeBinding.rvRecentChats.layoutManager =LinearLayoutManager(activity)

            recentChatAdapter.setOnRecentChatList(it)
            homeBinding.rvRecentChats.adapter= recentChatAdapter

        })

        recentChatAdapter.setOnRecentChatListener(this)

        circleImageView.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_homeFragment_to_settingFragment)
        }
    }

    override fun onUserSelected(position: Int, users: Users) {
        // Chuyển hướng đến ChatFragment
        val actionToChatFragment = HomeFragmentDirections.actionHomeFragmentToChatFragment(users)
        view?.findNavController()?.navigate(actionToChatFragment)

        Log.e("HomeFragment", "onUserSelected: ${users.username}")
    }


    override fun getRecentChat(position: Int, recentChats: RecentChats) {

        val action = HomeFragmentDirections.actionHomeFragmentToChatFromHomeFragment(recentChats)
        view?.findNavController()?.navigate(action)

    }


}