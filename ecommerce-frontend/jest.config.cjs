module.exports = {
    testEnvironment: 'jsdom',
    setupFilesAfterEnv: ['<rootDir>/src/test/setupTests.js'],
    moduleNameMapper: {
        '\\.(css|less|scss|sass)$': '<rootDir>/src/test/styleMock.js',
    },
};
