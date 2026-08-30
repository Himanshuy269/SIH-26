# SIH-26
## How It Works

The system uses **GNSS + INS sensor fusion** to provide continuous and reliable positioning.

- When **GNSS is available**, GNSS data is combined with the **Inertial Navigation System (INS)** to determine an accurate position.
- When **GNSS is lost** due to tunnels, underpasses, dense urban areas, forests, etc., the system switches to **dead reckoning**.
- During the GNSS outage, the **IMU (accelerometer + gyroscope)** provides motion data to the INS, which estimates the vehicle's movement and continuously calculates its position.
- Since INS-based positioning gradually accumulates **drift/error**, the estimated position becomes less accurate over time.
- When **GNSS becomes available again**, the system fuses the new GNSS measurements with the INS estimate to **correct the accumulated drift**.
- The system then returns to an accurate **GNSS + INS fused position**.

### Workflow

**GNSS Available → GNSS + INS → Accurate Position → GNSS Lost → INS + IMU → Dead Reckoning → Estimated Position → GNSS Returns → GNSS + INS → Drift Correction → Accurate Position**