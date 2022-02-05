import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:platon_fans/pages/asset/detail.dart';
import 'package:flutter/services.dart';

import 'package:fluttertoast/fluttertoast.dart';
import 'dart:convert' as convert;

import '../messageListView.dart';
class HomeAssetView extends StatefulWidget {

  @override
  HomeAssetViewState createState() {
    return new HomeAssetViewState();
  }
}

class HomeAssetViewState extends State<HomeAssetView>{
  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            "首页资产管理",
            style: TextStyle(
                fontFamily: "Sofia",
                color: Colors.white
            ),
          ),
          leading: IconButton(
            icon: Icon(Icons.chevron_left),
            color: Colors.white,
            onPressed: () {
              Navigator.pop(context,"detail");
            },
          ),
        ),
        backgroundColor: Colors.white,
        body: SingleChildScrollView(
            child:Column(
              mainAxisAlignment: MainAxisAlignment.start,
              children: <Widget>[

                Container(
                  height: 2,
                  color: Colors.black38,
                ),
                SizedBox(
                  height: 20,
                ),
                Container(
                  height: 40,
                  alignment: Alignment.topLeft,
                  padding: const EdgeInsets.only(left: 15.0, right: 15.0, ),
                  child: Text("首页资产",style: TextStyle(fontSize: 18),),
                ),
                Container(
                  height: MediaQuery.of(context).size.height-360,
                  width: MediaQuery.of(context).size.width,
                  child:
                  ListView.builder(
                    //controller: _controller,
                    itemCount: 2,
                    itemBuilder: (BuildContext context, int position) {

                      return  _list(position, Icons.gamepad, "aaa", "asdf","asdf",1);
                    },
                  ),
                )

              ],
            )
        )
    );
  }
  Widget _list(int position, IconData icon, String symbol,  String value, String id, int assetType) {
    return
      Container(
          height: 80.0,
          child:
          Column(
              children: <Widget>[
                Padding(
                  padding: const EdgeInsets.only(left: 25.0, right: 25.0,bottom: 10 ),
                  child:
                  InkWell(
                    onTap: () {
                      // Navigator.of(context).push(PageRouteBuilder(
                      //    pageBuilder: (_, __, ___) => new AssetDetailView()));

                    },
                    child:Column(
                      children: <Widget>[
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: <Widget>[

                            Row(
                              children: <Widget>[
                                InkWell(
                                    onTap: () {
                                      //showDeleteDialog(id);
                                    },
                                    child:Container(
                                      width: 45,
                                      height: 45,
                                      margin: const EdgeInsets.only( right: 0,),
                                      child: Icon(Icons.remove_circle,color:Colors.red ),)
                                ),
                                Container(
                                  height: 50.0,
                                  width: 50.0,
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.all(Radius.circular(40.0)),
                                    //color: Colors.indigo,
                                  ),
                                  child: Center(
                                    child: Image(
                                      width:50,
                                      height:50,
                                      image:  AssetImage("assets/images/main.png") ,
                                    ),
                                  ),
                                ),
                                SizedBox(
                                  width: 12.0,
                                ),
                                Column(
                                    children: <Widget>[
                                      Text(
                                        symbol,
                                        style: TextStyle(
                                            color: Colors.black,
                                            fontFamily: "Popins",
                                            // fontWeight: FontWeight.w600,
                                            fontSize: 20),
                                      ),
                                      Text(
                                        symbol,
                                        style: TextStyle(
                                            color: Colors.black54,
                                            fontFamily: "Popins",
                                            // fontWeight: FontWeight.w600,
                                            fontSize: 20),
                                      ),
                                    ])

                              ],
                            ),
                            Row(
                                children: <Widget>[

                                  //isSetting && position != 0?
                                  InkWell(
                                      onTap: () {
                                        //showDeleteDialog(id);
                                      },
                                      child:Container(
                                        width: 50,
                                        height: 50,
                                        margin: const EdgeInsets.only( right: 0,),
                                        child: Icon(Icons.format_list_bulleted_rounded,color:Colors.black ),)
                                  )
                                ])

                          ],
                        ),



                      ],
                    ),
                  ),
                ),Container(
                    height: 2,
                    color: Color(0xFFABABAB).withOpacity(0.3)
                ),
              ])

      );
  }


}