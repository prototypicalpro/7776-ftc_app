package org.firstinspires.ftc.teamcode.opmodes.pointer;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opmodes.ADPSAuto;

/**
 * Created by Noah on 12/31/2017.
 */

@Autonomous(name="Red Front Auto", group="test")
public class RedAPDSAuto extends ADPSAuto {
    @Override
    public void _flipBits() {
        red = true;
    }
}
