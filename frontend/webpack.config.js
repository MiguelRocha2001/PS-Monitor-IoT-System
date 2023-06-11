const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    mode: "development",
    resolve: {
        extensions: [".js", ".ts", ".tsx"]
    },
    devServer: {
        historyApiFallback: true,
        proxy: {
            '/api': {
              target: 'https://localhost:9000/api',
              secure: false,//TO USE SELF SIGNED CERTIFICATE
              target: 'http://localhost:9000',
            },
          },
    },
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/
            },
            {
                test: /\.css$/,
                use: [
                    'style-loader',
                    'css-loader'
                ]
            }
        ]
    },

    plugins: [
        new HtmlWebpackPlugin({
            template : './public/index.html'
        })
    ]
}

