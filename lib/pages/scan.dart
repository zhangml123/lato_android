import 'package:flutter/material.dart';

import 'package:scan/scan.dart';
class Scan extends StatefulWidget {
  ScanViewState createState() => ScanViewState();
}

class ScanViewState extends State<Scan>{


  @override
  Widget build(BuildContext context) {
    ScanController controller = ScanController();
    return new Scaffold(
        body: Container(
          alignment: Alignment.center,
          width: MediaQuery.of(context).size.width, // custom wrap size
          height: MediaQuery.of(context).size.height,
          child: ScanView(
            controller: controller,
// custom scan area, if set to 1.0, will scan full area
            scanAreaScale: 1.0,
            scanLineColor: Colors.blue,
            onCapture: (data) {
              Navigator.pop(context,data);
            },
          ),
        ),

    );
  }
}