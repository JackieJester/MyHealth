package ro.georgemarinescu.myhealth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_main_screen.*
import kotlinx.android.synthetic.main.fragment_main_screen.view.*
import kotlinx.android.synthetic.main.layout_card_list_item.*
import ro.georgemarinescu.myhealth.models.CardPost
import ro.georgemarinescu.myhealth.models.DataSource


class MainScreenFragment : Fragment(), OnCardItemClickListner {

    private lateinit var cardAdapter: RecyclerAdapter
    private var backPressedTime = 0L
    public lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("MainScreen", "Fragment back pressed invoked")
                    if(backPressedTime + 2000 > System.currentTimeMillis()){
                        activity!!.finish()

                    }else{
                        Toast.makeText(context,"Press back again to exit", Toast.LENGTH_SHORT).show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
            }
            )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.logOut_action){
            Toast.makeText(context,"Logout pressed", Toast.LENGTH_SHORT).show()
            auth.signOut()
            findNavController().popBackStack()


        }
        if(id == R.id.profile_action){
            Toast.makeText(context,"Profile pressed", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.accountFragment)

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        addDataSet()
    }
    private fun addDataSet(){
        val data = DataSource.createDataSet()
        cardAdapter.submitList(data)
    }
    private fun initRecyclerView() {
        view?.recycler_view!!.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecoration = TopSpacingItemDecoration( 30)
            addItemDecoration(topSpacingDecoration)
            cardAdapter = RecyclerAdapter(this@MainScreenFragment)
            adapter = cardAdapter
        }
    }

    override fun onItemClick(item: CardPost, position: Int) {
        val bundle = bundleOf("title" to item.title)
        findNavController().navigate(R.id.specialisationFragment,bundle)
    }

    }



