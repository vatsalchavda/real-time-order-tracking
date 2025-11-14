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
        return 'bg-gradient-to-r from-yellow-400 to-orange-400 text-white'
      case 'CONFIRMED':
        return 'bg-gradient-to-r from-green-400 to-emerald-500 text-white'
      case 'CANCELLED':
        return 'bg-gradient-to-r from-red-400 to-pink-500 text-white'
      default:
        return 'bg-gradient-to-r from-gray-400 to-gray-500 text-white'
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 py-8">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="text-center mb-12">
          <div className="inline-block mb-4">
            <div className="bg-gradient-to-r from-blue-600 to-purple-600 text-white px-6 py-2 rounded-full text-sm font-semibold shadow-lg">
              🚀 Event-Driven Architecture
            </div>
          </div>
          <h1 className="text-5xl font-bold bg-gradient-to-r from-blue-600 via-purple-600 to-pink-600 bg-clip-text text-transparent mb-3">
            Order Management System
          </h1>
          <p className="text-gray-600 text-lg">
            SpringBoot • React • MongoDB • RabbitMQ
          </p>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Create Order Form */}
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-xl p-8 border border-gray-100">
            <div className="flex items-center mb-6">
              <div className="bg-gradient-to-r from-blue-500 to-purple-500 p-3 rounded-xl mr-4">
                <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                </svg>
              </div>
              <h2 className="text-2xl font-bold text-gray-800">Create New Order</h2>
            </div>
            
            <form onSubmit={createOrder} className="space-y-5">
              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  👤 Customer Name
                </label>
                <input
                  type="text"
                  value={customerName}
                  onChange={(e) => setCustomerName(e.target.value)}
                  required
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                  placeholder="John Doe"
                />
              </div>

              <div>
                <label className="block text-sm font-semibold text-gray-700 mb-2">
                  📦 Product Name
                </label>
                <input
                  type="text"
                  value={productName}
                  onChange={(e) => setProductName(e.target.value)}
                  required
                  className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                  placeholder="Laptop"
                />
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    🔢 Quantity
                  </label>
                  <input
                    type="number"
                    value={quantity}
                    onChange={(e) => setQuantity(Number(e.target.value))}
                    min="1"
                    required
                    className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-gray-700 mb-2">
                    💰 Price ($)
                  </label>
                  <input
                    type="number"
                    value={price}
                    onChange={(e) => setPrice(Number(e.target.value))}
                    min="0"
                    step="0.01"
                    required
                    className="w-full px-4 py-3 border-2 border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent transition-all"
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full bg-gradient-to-r from-blue-600 to-purple-600 text-white py-4 px-6 rounded-xl hover:from-blue-700 hover:to-purple-700 focus:outline-none focus:ring-4 focus:ring-purple-300 disabled:opacity-50 disabled:cursor-not-allowed font-semibold text-lg shadow-lg transform hover:scale-[1.02] transition-all"
              >
                {loading ? '⏳ Creating...' : '✨ Create Order'}
              </button>
            </form>
          </div>

          {/* Orders List */}
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-xl p-8 border border-gray-100">
            <div className="flex justify-between items-center mb-6">
              <div className="flex items-center">
                <div className="bg-gradient-to-r from-green-500 to-emerald-500 p-3 rounded-xl mr-4">
                  <svg className="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                </div>
                <h2 className="text-2xl font-bold text-gray-800">Recent Orders</h2>
              </div>
              <button
                onClick={fetchOrders}
                className="bg-gradient-to-r from-blue-500 to-purple-500 text-white px-4 py-2 rounded-lg hover:from-blue-600 hover:to-purple-600 text-sm font-semibold shadow-md transform hover:scale-105 transition-all"
              >
                🔄 Refresh
              </button>
            </div>

            <div className="space-y-4 max-h-96 overflow-y-auto pr-2">
              {orders.length === 0 ? (
                <div className="text-center py-12">
                  <div className="text-6xl mb-4">📦</div>
                  <p className="text-gray-500 text-lg">
                    No orders yet. Create your first order!
                  </p>
                </div>
              ) : (
                orders.map((order) => (
                  <div
                    key={order.orderId}
                    className="bg-gradient-to-r from-white to-gray-50 border-2 border-gray-100 rounded-xl p-5 hover:shadow-lg hover:border-purple-200 transition-all transform hover:scale-[1.02]"
                  >
                    <div className="flex justify-between items-start mb-3">
                      <div>
                        <p className="font-bold text-gray-900 text-lg">
                          {order.customerName}
                        </p>
                        <p className="text-sm text-gray-500 font-mono">
                          #{order.orderId.substring(0, 8)}
                        </p>
                      </div>
                      <span
                        className={`px-3 py-1 rounded-full text-xs font-bold shadow-md ${getStatusColor(
                          order.status
                        )}`}
                      >
                        {order.status}
                      </span>
                    </div>
                    <div className="flex justify-between items-center pt-3 border-t border-gray-200">
                      <span className="text-lg font-bold text-green-600">
                        ${order.totalAmount.toFixed(2)}
                      </span>
                      <span className="text-sm text-gray-500">
                        📅 {new Date(order.createdAt).toLocaleDateString()}
                      </span>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>
        </div>

        {/* System Info */}
        <div className="mt-8 bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 rounded-2xl p-1 shadow-xl">
          <div className="bg-white rounded-xl p-6">
            <div className="flex items-start">
              <div className="text-4xl mr-4">🏗️</div>
              <div>
                <h3 className="font-bold text-gray-900 text-xl mb-2">
                  Enterprise-Grade Architecture
                </h3>
                <p className="text-gray-700 leading-relaxed">
                  This application demonstrates <span className="font-semibold text-purple-600">event-driven microservices architecture</span> with 
                  <span className="font-semibold text-blue-600"> SpringBoot</span>, 
                  <span className="font-semibold text-green-600"> MongoDB</span>, and 
                  <span className="font-semibold text-orange-600"> RabbitMQ</span>. 
                  Orders are processed asynchronously through message queues, implementing the 
                  <span className="font-semibold text-pink-600"> Saga pattern</span> for distributed transactions.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

export default App

// Made with Bob
