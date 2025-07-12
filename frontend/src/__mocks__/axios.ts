// Mock implementation for axios using proper Jest mock functions
const mockAxios: any = {
  get: jest.fn(() => Promise.resolve({ data: [] })),
  post: jest.fn(() => Promise.resolve({ data: {} })),
  put: jest.fn(() => Promise.resolve({ data: {} })),
  delete: jest.fn(() => Promise.resolve({ data: {} })),
  patch: jest.fn(() => Promise.resolve({ data: {} })),
  create: jest.fn().mockReturnValue(undefined),
  defaults: {
    baseURL: '',
    headers: {}
  },
  interceptors: {
    request: {
      use: jest.fn()
    },
    response: {
      use: jest.fn()
    }
  }
};

// Set up the create method to return the mock itself
mockAxios.create.mockReturnValue(mockAxios);

export default mockAxios;
