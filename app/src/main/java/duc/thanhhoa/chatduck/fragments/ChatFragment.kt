package duc.thanhhoa.chatduck.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
import duc.thanhhoa.chatduck.databinding.FragmentChatBinding
import duc.thanhhoa.chatduck.modal.Messages
import duc.thanhhoa.chatduck.modal.RecentChats
import duc.thanhhoa.chatduck.mvvm.ChatAppViewModel


class ChatFragment : Fragment() {


    lateinit var args: ChatFragmentArgs
    lateinit var binding : FragmentChatBinding

    lateinit var viewModel : ChatAppViewModel
    lateinit var adapter : MessageAdapter
    lateinit var toolbar: Toolbar





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view.findViewById(R.id.toolBarChat1)
        val circleImageView = toolbar.findViewById<CircleImageView>(R.id.chatImageViewUser1)
        val textViewName = toolbar.findViewById<TextView>(R.id.chatUserName1)
        val textViewStatus = view.findViewById<TextView>(R.id.chatUserStatus1)
        val chatBackBtn = toolbar.findViewById<ImageView>(R.id.chatBackBtn1)

        viewModel = ViewModelProvider(this).get(ChatAppViewModel::class.java)


        args = ChatFragmentArgs.fromBundle(requireArguments())

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner



        Glide.with(view.getContext()).load(args.users.imageUrl!!).placeholder(R.drawable.person).dontAnimate().into(circleImageView);
        textViewName.setText(args.users.username)
        textViewStatus.setText(args.users.status)


        chatBackBtn.setOnClickListener {


            view.findNavController().navigate(R.id.action_chatFragment_to_homeFragment)

        }

        binding.sendBtn1.setOnClickListener {


            viewModel.sendMessage(Utils.getUidLoggedIn(), args.users.userid!!, args.users.username!!, args.users.imageUrl!!)





        }

        viewModel.getMessages(args.users.userid!!).observe(viewLifecycleOwner, Observer {

            initRecyclerView(it)
        })





    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initRecyclerView(it: List<Messages>) {

        adapter = MessageAdapter()

        val layoutManager = LinearLayoutManager(context)

        binding.messagesRecyclerView1.layoutManager = layoutManager
        layoutManager.stackFromEnd = true

        adapter.setMessageList(it)
        adapter.notifyDataSetChanged()
        binding.messagesRecyclerView1.adapter = adapter

    }


}