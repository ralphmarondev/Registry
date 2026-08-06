<template>
	<div class="min-h-screen flex flex-col bg-linear-to-br from-emerald-50 via-white to-green-50 animate-fade-in">
		<header class="fixed top-0 left-0 right-0 z-50 bg-white/80 backdrop-blur-md border-b border-emerald-100/50">
			<div class="container mx-auto px-6">
				<div class="max-w-6xl mx-auto flex items-center justify-between py-4">
					<div class="flex items-center gap-3 group cursor-pointer">
						<div class="w-10 h-10 bg-linear-to-br from-emerald-600 to-green-500 rounded-xl flex items-center justify-center shadow-lg shadow-emerald-500/25 group-hover:shadow-emerald-500/40 group-hover:scale-105 transition-all duration-300">
							<span class="text-white font-bold text-lg">E</span>
						</div>
						<span class="text-2xl font-bold bg-linear-to-r from-emerald-700 to-green-600 bg-clip-text text-transparent group-hover:from-emerald-600 group-hover:to-green-500 transition-all duration-300">
							EProfile
						</span>
					</div>
					<router-link
							to="/login"
							class="px-8 py-3 bg-linear-to-r from-emerald-600 to-green-500 text-white font-semibold rounded-full hover:shadow-xl hover:shadow-emerald-500/40 hover:scale-105 transition-all duration-300 ease-in-out">
						Login
					</router-link>
				</div>
			</div>
		</header>

		<main class="flex-1 flex items-center pt-24 pb-12">
			<div class="container mx-auto px-6">
				<div class="max-w-6xl mx-auto">
					<div class="grid lg:grid-cols-2 gap-16 items-center">
						<!-- Left Content -->
						<div class="space-y-6 order-2 lg:order-1 text-center lg:text-left">
							<div class="inline-flex items-center gap-2 px-4 py-2 bg-emerald-100/80 backdrop-blur-sm rounded-full text-emerald-700 text-sm font-medium">
								<span class="relative flex h-2 w-2">
									<span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
									<span class="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
								</span>
								Municipality of Gonzaga
							</div>

							<!-- Main Heading -->
							<h1 class="text-4xl md:text-5xl lg:text-6xl font-extrabold leading-tight">
								<span class="text-gray-800">Profiling</span>
								<br>
								<span class="bg-linear-to-r from-emerald-600 to-green-500 bg-clip-text text-transparent">Management</span>
								<span class="text-gray-800"> System</span>
							</h1>

							<!-- Subtitle -->
							<p class="text-xl text-emerald-600 font-semibold">
								Gonzaga, Cagayan
							</p>

							<!-- Description -->
							<p class="text-gray-600 leading-relaxed max-w-lg mx-auto lg:mx-0 text-base">
								A centralized digital platform for managing and maintaining
								profiling records within the Municipality of Gonzaga,
								Cagayan. The system provides secure, organized, and efficient
								record management to support faster services and informed
								decision-making.
							</p>
						</div>

						<!-- Right Illustration -->
						<div class="order-1 lg:order-2 flex justify-center">
							<div class="relative">
								<!-- Decorative circles -->
								<div class="absolute -top-10 -right-10 w-48 h-48 bg-emerald-200/30 rounded-full blur-3xl"></div>
								<div class="absolute -bottom-10 -left-10 w-48 h-48 bg-green-200/30 rounded-full blur-3xl"></div>

								<!-- Simple image with shadow -->
								<img src="/favicon.svg"
								     alt="Profiling System"
								     class="w-72 h-72 object-contain animate-float drop-shadow-2xl">
							</div>
						</div>
					</div>
				</div>
			</div>
		</main>

		<footer class="bg-white/50 backdrop-blur-sm border-t border-emerald-100/50">
			<div class="container mx-auto px-6">
				<div class="max-w-6xl mx-auto py-6 text-center text-sm text-gray-500">
					© 2026 Municipality of Gonzaga, Cagayan. All Rights Reserved.
				</div>
			</div>
		</footer>
	</div>
</template>

<style scoped>
@keyframes float {
	0%, 100% {
		transform: translateY(0px);
	}
	50% {
		transform: translateY(-16px);
	}
}

@keyframes fadeIn {
	from {
		opacity: 0;
		transform: translateY(20px);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

.animate-float {
	animation: float 5s ease-in-out infinite;
}

.animate-fade-in {
	animation: fadeIn 0.8s ease-out;
}
</style>