/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventory.databinding.ItemListFragmentBinding

/**
 * Main fragment displaying details for all items in the database.
 */
class ItemListFragment : Fragment() {

    val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }

    private var _binding: ItemListFragmentBinding? = null
    private val binding get() = _binding!!
    // where you inflate the layout. The fragment has entered the CREATED state
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ItemListFragmentBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        //        val image = findViewById<View>(R.id.i) as ImageView
//        val bMap = BitmapFactory.decodeResource(context?.resources, R.drawable.img_20220227_113828)
//        image.setImageBitmap(bMap)
        //default time zone
        //default time zone
//        val date: Date = Date()
//        val formatter: SimpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
//        Log.d("Date",formatter.format(date))
//        val uri: Uri = Uri.parse("android.resource://your.package.here/drawable/img_20220227_113828.jpg")
//        val stream: InputStream = getContentResolver().openInputStream(uri)
//        viewModel.addNewItem(
//            "eric",
//            "陳遠謀",
//            Date(),
//            "0968920081",
//            "wow90327@gmail.com"
//        )

        return binding.root
    }

    // creates a menu.xml first, then inflate it here
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.star_menu, menu)
    }

    // determine if the star button is selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // if == false, then show ic_star icon, and set value to true
        // else show ic_star_border icon, and value = false
        if (viewModel.isFav.value == false) {
            item.setIcon(R.drawable.ic_star)
            viewModel.isFav.value = true
        } else {
            item.setIcon(R.drawable.ic_star_border)
            viewModel.isFav.value = false
        }
        return super.onOptionsItemSelected(item)
    }

    // called after the view is created. In this method, bind specific views to properties
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ItemListAdapter(viewModel = viewModel) {
            val action =
                ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(it.id,it.vocFavorite)
            this.findNavController().navigate(action)
        }

        binding.recyclerView.adapter = adapter
        
        // should observe another issue: Read Item
        /*viewModel.retrieveItem.observe(this, {
            adapter.setData(it)
        })*/

        viewModel.isFav.observe(this.viewLifecycleOwner) {
            viewModel.setData(viewModel.isFav.value!!)
            viewModel.allItems.observe(this.viewLifecycleOwner) { items ->
                items.let {
                    adapter.submitList(it)
                }
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.floatingActionButton.setOnClickListener {
            val action = ItemListFragmentDirections.actionItemListFragmentToAddItemFragment(
                "Add New Words", 0,false
            )
            this.findNavController().navigate(action)
        }
    }
}
