import { useState, useEffect } from 'react'
import './App.css'

interface Order {
  orderId: string
  customerName: string
  status: string
  totalAmount: number
  createdAt: string
}

function App() {
  const [orders, setOrders] = useState<Order[]>([])
  const [loading, setLoading] = useState(false)
  const [customerName, setCustomerName] = useState('')
  const [productName, setProductName] = useState('')
  const [quantity, setQuantity] = useState(1)
  const [price, setPrice] = useState(0)

  const API_URL = import.meta.env.VITE_API_URL || '/api'

  useEffect(() => {
    fetchOrders()
  }, [])

  const fetchOrders = async () => {
    try {
      const response = await fetch(`${API_URL}/orders`)
      const data = await response.json()
      setOrders(data)
    } catch (error) {
      console.error('Error fetching orders:', error)
    }
  }

  const createOrder = async (e: React.FormEvent) => {
    e.preventDefault()
    setLoading(true)

    try {
      const response = await fetch(`${API_URL}/orders`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          customerId: `CUST${Date.now()}`,
          customerName,
          items: [
            {
              productId: `PROD${Date.now()}`,
              productName,
              quantity,
              price,
            },
          ],
          shippingAddress: '123 Main St',
        }),
      })

      if (response.ok) {
        setCustomerName('')
        setProductName('')
        setQuantity(1)
        setPrice(0)
        fetchOrders()
      }
    } catch (error) {
      console.error('Error creating order:', error)
    } finally {
      setLoading(false)
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING':
        return 'bg-warning text-dark'
      case 'CONFIRMED':
        return 'bg-success text-white'
      case 'CANCELLED':
        return 'bg-danger text-white'
      default:
        return 'bg-secondary text-white'
    }
  }

  return (
    <div className="min-h-screen bg-light py-4">
      <div className="container">
        {/* Header */}
        <div className="text-center mb-4">
          <h1 className="display-4 fw-bold text-primary mb-2">
            Order Management System
          </h1>
          <p className="text-muted">
            Event-Driven Microservices Architecture
          </p>
        </div>

        <div className="row g-4">
          {/* Create Order Form */}
          <div className="col-lg-6">
            <div className="card shadow-sm">
              <div className="card-header bg-primary text-white">
                <h5 className="card-title mb-0">Create New Order</h5>
              </div>
              <div className="card-body">
                <form onSubmit={createOrder}>
                  <div className="mb-3">
                    <label className="form-label">Customer Name</label>
                    <input
                      type="text"
                      className="form-control"
                      value={customerName}
                      onChange={(e) => setCustomerName(e.target.value)}
                      required
                      placeholder="John Doe"
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Product Name</label>
                    <input
                      type="text"
                      className="form-control"
                      value={productName}
                      onChange={(e) => setProductName(e.target.value)}
                      required
                      placeholder="Laptop"
                    />
                  </div>

                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label className="form-label">Quantity</label>
                      <input
                        type="number"
                        className="form-control"
                        value={quantity}
                        onChange={(e) => setQuantity(Number(e.target.value))}
                        min="1"
                        required
                      />
                    </div>

                    <div className="col-md-6 mb-3">
                      <label className="form-label">Price ($)</label>
                      <input
                        type="number"
                        className="form-control"
                        value={price}
                        onChange={(e) => setPrice(Number(e.target.value))}
                        min="0"
                        step="0.01"
                        required
                      />
                    </div>
                  </div>

                  <button
                    type="submit"
                    className="btn btn-primary w-100"
                    disabled={loading}
                  >
                    {loading ? 'Creating...' : 'Create Order'}
                  </button>
                </form>
              </div>
            </div>
          </div>

          {/* Orders List */}
          <div className="col-lg-6">
            <div className="card shadow-sm">
              <div className="card-header bg-success text-white d-flex justify-content-between align-items-center">
                <h5 className="card-title mb-0">Recent Orders</h5>
                <button
                  onClick={fetchOrders}
                  className="btn btn-sm btn-light"
                >
                  Refresh
                </button>
              </div>
              <div className="card-body">
                <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
                  {orders.length === 0 ? (
                    <div className="text-center text-muted py-5">
                      <p>No orders yet. Create your first order!</p>
                    </div>
                  ) : (
                    <div className="list-group">
                      {orders.map((order) => (
                        <div
                          key={order.orderId}
                          className="list-group-item list-group-item-action mb-2"
                        >
                          <div className="d-flex w-100 justify-content-between align-items-start mb-2">
                            <div>
                              <h6 className="mb-1">{order.customerName}</h6>
                              <small className="text-muted">
                                Order #{order.orderId.substring(0, 8)}
                              </small>
                            </div>
                            <span className={`badge ${getStatusColor(order.status)}`}>
                              {order.status}
                            </span>
                          </div>
                          <div className="d-flex w-100 justify-content-between">
                            <strong className="text-success">
                              ${order.totalAmount.toFixed(2)}
                            </strong>
                            <small className="text-muted">
                              {new Date(order.createdAt).toLocaleDateString()}
                            </small>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* System Info */}
        <div className="alert alert-info mt-4" role="alert">
          <h5 className="alert-heading">System Architecture</h5>
          <p className="mb-0">
            This application demonstrates event-driven microservices architecture
            with SpringBoot, MongoDB, and RabbitMQ. Orders are processed
            asynchronously through message queues, implementing the Saga pattern
            for distributed transactions.
          </p>
        </div>
      </div>
    </div>
  )
}

export default App
