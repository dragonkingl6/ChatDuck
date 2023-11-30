package duc.thanhhoa.chatduck.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import duc.thanhhoa.chatduck.R
import duc.thanhhoa.chatduck.Utils
import duc.thanhhoa.chatduck.adapter.MessageAdapter
import duc.thanhhoa.chatduck.databinding.FragmentChatfromHomeBinding
import duc.thanhhoa.chatduck.modal.Messages
import duc.thanhhoa.chatduck.mvvm.ChatAppViewModel


class ChatFromHomeFragment : Fragment() {

    lateinit var args : ChatFromHomeFragmentArgs
    lateinit var binding: FragmentChatfromHomeBinding
    lateinit var viewModel : ChatAppViewModel
    lateinit var toolbar: Toolbar
    lateinit var adapter : MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatfrom_home, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolBarChat)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName)


        args = ChatFromHomeFragmentArgs.fromBundle(requireArguments())


        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)


        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        Glide.with(view.getContext()).load(args.recentChat.friendsimage!!).placeholder(R.drawable.person).dontAnimate().into(circleImageView);
        textViewName.setText(args.recentChat.name)
        //textViewStatus.setText(args.users.status)



        binding.chatBackBtn.setOnClickListener {


            view.findNavController().navigate(R.id.action_chatFromHomeFragment_to_homeFragment)

        }

        binding.sendBtn.setOnClickListener {


            viewModel.sendMessage(Utils.getUidLoggedIn(), args.recentChat.friendid!!, args.recentChat.name!!, args.recentChat.friendsimage!!)





        }


        viewModel.getMessages(args.recentChat.friendid!!).observe(viewLifecycleOwner, Observer {





            initRecyclerView(it)



        })




    }

    private fun initRecyclerView(list: List<Messages>) {


        adapter = MessageAdapter()

        val layoutManager = LinearLayoutManager(context)

        binding.messagesRecyclerView.layoutManager = layoutManager
        layoutManager.stackFromEnd = true

        adapter.setMessageList(list)
        adapter.notifyDataSetChanged()
        binding.messagesRecyclerView.adapter = adapter



    }
}