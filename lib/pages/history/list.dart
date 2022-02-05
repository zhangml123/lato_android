import 'package:flutter/material.dart';
import 'package:platon_fans/generated/l10n.dart';
import 'package:platon_fans/pages/history/detail.dart';

class HistoryListView extends StatefulWidget {
  final List transactions;
  final String address;
  final Function() refreshHistory;
  final Function() loadMore;
  HistoryListView(this.transactions, this.address, this.refreshHistory, this.loadMore) : super();
  @override
  HistoryListViewState createState() {
    return new HistoryListViewState();
  }
}

class HistoryListViewState extends State<HistoryListView> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  ScrollController _controller = new ScrollController();

  late List _transactions;
  late String _address = "";
  late Function() _refreshHistory;
  late Function() _loadMore;
  @override
  void initState() {
    super.initState();
    _tabController = TabController(vsync: this,  length: 3);
    _transactions = widget.transactions;
    _refreshHistory = widget.refreshHistory;
    _loadMore = widget.loadMore;
    print("HistoryListView  transactions= "+ _transactions.toString());
    _controller.addListener(() {
      print("_controller.addListener ");
      if (_controller.position.pixels == _controller.position.maxScrollExtent) {
        _loadMoreData();
      }
    });

  }
  Future<Null> _loadMoreData(){
    return Future.delayed(Duration(seconds: 1),(){
      print("HistoryListView  _loadMoreData" );
      _loadMore();
    });
  }
  @override
  void didUpdateWidget(HistoryListView oldWidget) {
    super.didUpdateWidget(oldWidget);
    print("didUpdateWidget");
    print("HistoryListView  transactions11= "+ widget.transactions.toString());
    if(oldWidget.transactions != widget.transactions){
      this.setState(() {
        _transactions = widget.transactions;
      });
    }
    if(oldWidget.address != widget.address){
      this.setState(() {
        _address = widget.address;
      });
    }
    print("HistoryListView  _address= "+ _address);

  }
  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }
  @override
  Widget build(BuildContext context) {

    return LayoutBuilder(
        builder: (context, constrains){
          return Scaffold(
      backgroundColor: Colors.white,
      body:  Column(
          children: <Widget>[
            Container(
              height: 50.0,
              width: MediaQuery.of(context).size.width,
              child: Padding(
                padding:
                const EdgeInsets.only(left: 20.0, right: 0.0, top: 8.0, bottom: 5),
                child: Text(S.of(context).transactionHistory,
                  style:TextStyle(fontSize: 18)
                )
              ),
            ),
            Container(
              height: constrains.maxHeight - 50,
              width: MediaQuery.of(context).size.width,
              child:
              RefreshIndicator(
                onRefresh: _onRefresh,
                displacement: 40,
                color: Colors.blue,
                backgroundColor: Colors.white,
                notificationPredicate: defaultScrollNotificationPredicate,
                child: ListView.builder(
                  controller: _controller,
                  itemCount: _transactions.length,
                  itemBuilder: (BuildContext context, int position) {
                    Map<String, dynamic> transaction =  _transactions[position];
                    return _list(transaction);
                  },
                  physics: new AlwaysScrollableScrollPhysics(),
                ),
              ),

            ),

          ],
        ),
    );
        });
  }
  Future<Null> _onRefresh(){
    return Future.delayed(Duration(seconds: 1),(){   // 延迟5s完成刷新
      _refreshHistory();
    });
  }
  Widget _list(Map<String, dynamic> transaction) {
    String type = S.of(context).receive ;
    IconData icon = Icons.arrow_downward;
    Color color = Colors.cyan;
    if(_address != "" && _address == transaction["from"]){
      type = S.of(context).send ;
      icon = Icons.arrow_upward;
      color = Colors.redAccent;
    }
    String time = transaction["timestamp"].toString();
    String value = transaction["value"].toString();
    return Padding(
      padding: const EdgeInsets.only(left: 15.0, right: 15.0, bottom: 20.0),
      child:
      Container(
          height: 70.0,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.all(Radius.circular(15.0)),
            color: Colors.white,

          ),
          child:
          Padding(
            padding: const EdgeInsets.only(left: 15.0, right: 15.0, top: 15.0),
            child:
            InkWell(
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) {
                    return HistoryDetailView(transaction);
                  }),
                );
              },
              child:Column(
                children: <Widget>[
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: <Widget>[
                      Row(
                        children: <Widget>[
                          Container(
                            height: 40.0,
                            width: 40.0,
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.all(Radius.circular(40.0)),
                              color: color,
                            ),
                            child: Center(
                              child: Icon(
                                icon,
                                color: Colors.white,
                              ),
                            ),
                          ),
                          SizedBox(
                            width: 12.0,
                          ),
                          Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              crossAxisAlignment: CrossAxisAlignment.start,
                            children: <Widget>[
                              Text(
                                type,
                                style: TextStyle(
                                    color: Colors.black,
                                    fontFamily: "Popins",
                                    fontWeight: FontWeight.w600,
                                    fontSize: 15.5),
                              ),
                              Text(
                                time,
                                style: TextStyle(
                                    color: Colors.black54,
                                    fontFamily: "Popins",
                                    fontWeight: FontWeight.w600,
                                    fontSize: 15.5),
                              ),
                            ]
                          ),
                        ],
                      ),
                      Text(
                        value,
                        style: TextStyle(
                          color: color,
                          fontFamily: "Popins",
                          fontWeight: FontWeight.w700,
                          fontSize: 15.5,
                        ),
                      )
                    ],
                  ),
                ],
              ),
            ),
          )
      ),
    );
  }
}