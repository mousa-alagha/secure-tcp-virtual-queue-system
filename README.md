# Virtual Waiting Queue System – Secure Distributed TCP Architecture

This project implements a secure and dependable virtual waiting queue system using **TCP protocol**, with advanced authentication, session management, and real-time queue handling.

## 📁 Project Structure

| File | Description |
|------|-------------|
| Client.java         | TCP client that connects to the server, joins queues, and interacts with events. |
| Server.java         | Central server that manages events, queues, and client sessions securely. |
| Event.java          | Event model that defines queue logic, sessions, and client handling. |
| SecurityUtil.java   | Implements secure communication utilities and authentication logic. |
| StringUtil.java     | Utilities for string operations, possibly including hashing or formatting. |

## 🚀 Features

- Multi-client **TCP architecture** with real-time session management.
- **Secure communication** with authentication using `SecurityUtil`.
- **Pre-queue and FIFO queue** handling for fair and timed access.
- Resumable sessions and reconnection support.
- Simulation of real-life events like booking systems or ticket queues.

## 🔐 Security & Reliability

- Authentication support (potential for MFA).
- Queue resilience: clients can reconnect without losing queue position if session is active.
- Sessions auto-expire to ensure queue fluidity.
- Communication integrity ensured using secure string utilities.

## 📊 Performance & Evaluation

The system was tested with multiple concurrent users and event types. Metrics such as waiting time, queue position, and failed logins were logged and analyzed to validate system dependability and scalability.

## 🛠️ How to Run

1. Compile all `.java` files.
2. Start the `Server.java`.
3. Run multiple instances of `Client.java`.
4. Follow on-screen instructions to simulate different events and queue behavior.
