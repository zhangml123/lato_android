import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:platon_fans/generated/l10n.dart';
class HistoryDetailView extends StatefulWidget {
  Map<String, dynamic> transaction;
  HistoryDetailView(this.transaction) : super();
  @override
  HistoryDetailViewState createState() {
    return new HistoryDetailViewState();
  }
}

class HistoryDetailViewState extends State<HistoryDetailView>{
  late Map<String, dynamic> _transaction;
  @override
  void initState() {
    super.initState();
    _transaction = widget.transaction;
  }
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      appBar: AppBar(
          backgroundColor: Colors.blue,
          centerTitle: true,
          elevation: 0.5,
          title: Text(
            S.of(context).transactionDetail,
            style: TextStyle(
              fontFamily: "Sofia",
            ),
          ),
          leading: IconButton(
            icon: Icon(Icons.arrow_back),
            onPressed: () {
              Navigator.pop(context);
            },
          ),
          ),
      body: Container(
        height: MediaQuery.of(context).size.height,
        width: MediaQuery.of(context).size.width,
        decoration: BoxDecoration(
          color: Colors.white,
        ),
        child:
        Padding(
          padding:
          const EdgeInsets.only(left: 20.0, right: 20.0, top: 20.0, bottom: 25.0),
          child:Container(
            decoration: BoxDecoration(
              color: Colors.white,
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: <Widget>[
                Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    children: <Widget>[
                      Container(
                        height: 100.0,
                        width: 100.0,
                        decoration: BoxDecoration(
                          image:
                          DecorationImage(image: AssetImage("assets/images/success.png"), fit: BoxFit.fill),
                        ),
                      ),
                      SizedBox(height: 10.0),
                      Container(
                        alignment: Alignment.center,
                        child: Text(
                          _transaction["value"],
                          style:
                          TextStyle(fontFamily: "Sofia", color: Colors.green, fontSize: 26.0, fontWeight: FontWeight.w800),
                        ),
                      ),
                      SizedBox(height: 10.0),
                      Container(
                        alignment: Alignment.topLeft,
                        child: Text(
                          S.of(context).fromAddress+":",
                          style:
                          TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                        ),
                      ),

                      SizedBox(height: 5.0),
                      Row(children: [
                        Container(
                          width: MediaQuery.of(context).size.width - 80,
                          alignment: Alignment.topLeft,
                          child: Text(
                            _transaction["from"],
                            // maxLines: 5,
                            overflow: TextOverflow.fade,
                            softWrap: true,
                            style:
                            TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                          ),

                        ),
                        Container(
                            width: 25,
                            child:

                            IconButton(
                              icon: Icon(Icons.copy,size: 20,color:Colors.blue,),
                              onPressed: () {
                                Clipboard.setData(ClipboardData(text: _transaction["from"]));
                                Fluttertoast.showToast(
                                    msg: S.of(context).copySuccess,
                                    toastLength: Toast.LENGTH_SHORT,
                                    gravity: ToastGravity.BOTTOM,
                                    timeInSecForIosWeb: 1,
                                    backgroundColor: Colors.blue,
                                    textColor: Colors.white,
                                    fontSize: 16.0
                                );
                              },
                            ),
                          ),

                      ],),



                      SizedBox(height: 10.0),

                      Container(
                        alignment: Alignment.topLeft,
                        child: Text(
                          S.of(context).toAddress+":",
                          style:
                          TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                        ),
                      ),
                      SizedBox(height: 5.0),
                      Row(children: [
                      Container(

                        width: MediaQuery.of(context).size.width - 80,
                        alignment: Alignment.topLeft,
                        child: Text(
                          _transaction["txType"] == "2" ? _transaction["rAddress"] :_transaction["to"] ,
                          // maxLines: 5,
                          overflow: TextOverflow.fade,
                          softWrap: true,
                          style:
                          TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                        ),


                      ),Container(
                          width: 25,
                          child:

                          IconButton(
                            icon: Icon(Icons.copy,size: 20,color:Colors.blue,),
                            onPressed: () {
                              Clipboard.setData(ClipboardData(text: _transaction["to"]));
                              Fluttertoast.showToast(
                                  msg: S.of(context).copySuccess,
                                  toastLength: Toast.LENGTH_SHORT,
                                  gravity: ToastGravity.BOTTOM,
                                  timeInSecForIosWeb: 1,
                                  backgroundColor: Colors.blue,
                                  textColor: Colors.white,
                                  fontSize: 16.0
                              );
                            },
                          ),
                        ),

                      ]),

                      SizedBox(height: 10.0),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              S.of(context).time+":",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          SizedBox(width: 20.0),
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              _transaction["timestamp"].toString(),
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                        ],
                      ),
                      SizedBox(height: 10.0),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              S.of(context).transactionType+":",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          SizedBox(width: 20.0),
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                                _transaction["txType"],
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                        ],
                      ),

                      SizedBox(height: 10.0),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              S.of(context).amount+":",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          SizedBox(width: 20.0),
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              _transaction["value"] + _transaction["symbol"],
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                        ],
                      ),
                      SizedBox(height: 10.0),
                      /*Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              "手续费:",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          SizedBox(width: 20.0),
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              _transaction["fee"],
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                        ],
                      ),*/
                      SizedBox(height: 10.0),
                      /*Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              "确认区块:",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          SizedBox(width: 20.0),
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              _transaction["blockNumber"],
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                        ],
                      ),*/
                      SizedBox(height: 10.0),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Container(
                            alignment: Alignment.topLeft,
                            child: Text(
                              S.of(context).hash+":",
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          SizedBox(width: 20.0),
                          Container(
                            width: MediaQuery.of(context).size.width -150,
                            alignment: Alignment.topLeft,
                            child: Text(
                              _transaction["hash"],
                              overflow: TextOverflow.fade,
                              softWrap: true,
                              style:
                              TextStyle(fontFamily: "Sofia", color: Colors.black, fontSize: 16.0),
                            ),
                          ),
                          Container(
                            width: 25,
                            child:

                            IconButton(
                              icon: Icon(Icons.copy,size: 20,color:Colors.blue,),
                              onPressed: () {
                                Clipboard.setData(ClipboardData(text: _transaction["hash"]));
                                Fluttertoast.showToast(
                                    msg: S.of(context).copySuccess,
                                    toastLength: Toast.LENGTH_SHORT,
                                    gravity: ToastGravity.BOTTOM,
                                    timeInSecForIosWeb: 1,
                                    backgroundColor: Colors.blue,
                                    textColor: Colors.white,
                                    fontSize: 16.0
                                );
                              },
                            ),
                          ),
                        ],
                      ),



                    ]

                ),


              ],

            ),
          ),
        ),
      ),

    );
  }
}